/**
 * 
 */
package com.hjsj.hrms.transaction.general.inform.emp;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 *<p>Title:SaveUpdateDataTrans</p> 
 *<p>Description:保存更新记录</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-8-16:下午04:51:29</p> 
 *@author cmq
 *@version 4.0
 */
public class SaveUpdateDataTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm=this.getFormHM();
		String name=(String)hm.get("data_table_table");
		cat.debug("table name="+name);
		ArrayList list=(ArrayList)hm.get("data_table_record");

		String fieldsetid = name;
		ArrayList dblist=this.userView.getPrivDbList();
		for(int i=0;i<dblist.size();i++){
			String pre=(String)dblist.get(i);
			if(pre!=null&&pre.trim().length()>0){
				fieldsetid = fieldsetid.replace(pre,"");
			}
		}
		
		String fieldPri = this.userView.analyseTablePriv(fieldsetid);
		if(!"2".equals(fieldPri))
			throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("workdiary.message.update.record.competence")));
		
		FieldSet fieldset=DataDictionary.getFieldSetVo(fieldsetid);
		if(fieldset==null)
			throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("workdiary.message.no.record.update")));
		ArrayList itemlist = DataDictionary.getFieldList(fieldsetid,Constant.USED_FIELD_SET);
		try
		{
			HashSet hsset = new HashSet();
			DbNameBo db=new DbNameBo(this.getFrameconn());
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			for(int i=0;i<list.size();i++){
				RecordVo vo=(RecordVo)list.get(i);
				String B0110 = vo.getString("b0110");
				B0110=B0110!=null?B0110:"";
				String E0122 = vo.getString("e0122");
				E0122=E0122!=null?E0122:"";
				String E01A1 = vo.getString("e01a1");
				E01A1=E01A1!=null?E01A1:"";
				String A0100=vo.getString("a0100");
				if("2".equals(fieldPri)){
					for(int j=0;j<itemlist.size();j++){
						FieldItem fielditem = (FieldItem)itemlist.get(j);
						String itemPri = this.userView.analyseFieldPriv(fielditem.getItemid());
						if("2".equals(itemPri)&&fielditem.isFillable()){
							String itemvalues=vo.getString(fielditem.getItemid().toLowerCase());
							if(itemvalues==null||itemvalues.trim().length()<1)
								throw GeneralExceptionHandler.Handle(new Exception(fielditem.getItemdesc()+ResourceFactory.getProperty("workdiary.message.muster.input.not.null")));
						}
					}
				}
				int I9999=0;
				if(!fieldset.isMainset()){
					I9999 = vo.getInt("i9999");
//					if(I9999<1){
////						throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("workdiary.message.no.record.update")));
//					}
					
				}

				if(E0122.indexOf(B0110)==-1&&E0122.length()>0)
					throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("workdiary.message.org.includes.depart")));
				if(E01A1.indexOf(E0122)==-1&&name.indexOf("A01")!=-1&&E01A1.length()>0)
					throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("workdiary.message.org.includes.job")));
				RecordVo vo_old = null;
				if(fieldset.isMainset()){
					vo_old = db.getRecordVoA01(name,A0100);
					
					ArrayList listvalue = new ArrayList();
					listvalue.add(vo);
					dao.updateValueObject(listvalue);
					if(db.checkUpdate(vo,vo_old,fieldsetid)){
						db.updateMainSetA0100(name,B0110,E0122,vo_old,A0100);
					}
				}else{
					vo_old = db.getRecordVoA01(name,A0100,I9999);
					if(I9999<1){ //如果没有记录,则加入一条记录
						vo.setInt("i9999", 1);
						if(vo_old==null){
							dao.addValueObject(vo);
						}
					}else{
						ArrayList listvalue = new ArrayList();
						listvalue.add(vo);
						dao.updateValueObject(listvalue);
					}
					if(db.checkUpdate(vo,vo_old,fieldsetid)){
						db.updateMainSetA0100(name,B0110,E0122,vo_old,A0100,I9999+"");
					}
				}
				String unitdesc=AdminCode.getCodeName("UN",B0110);
				if("".equals(unitdesc)){
					unitdesc=AdminCode.getCodeName("UM",E0122);
				}

				boolean inflag=db.overWorkOut(name,E01A1,1);
				unitdesc=unitdesc+">>"+AdminCode.getCodeName("@K",E01A1);
				if(inflag){
					throw GeneralExceptionHandler.Handle(new GeneralException("",unitdesc+ResourceFactory.getProperty("workdiary.message.person.excess")+"！","",""));
				}
//				if(!fieldset.isMainset()){
////					hsset.add(A0100);
//				}
			}
			
//			if(fieldset.isMainset()){
//				sortMiant(dao,name);
//			}else{
//				sortItem(dao,name,hsset);
//			}
		}catch(Exception ex){
			throw GeneralExceptionHandler.Handle(ex);
		}

	}
	private void sortMiant(ContentDAO dao,String name){
		ArrayList sortlist = new ArrayList();
		try {
			this.frowset = dao.search("select A0100 from "+name);
			int i=1;
			while(this.frowset.next()){
				ArrayList list = new ArrayList();
				list.add(i+"");
				list.add(this.frowset.getString("A0100"));
				sortlist.add(list);
				i++;
			}
			String updatesql = "update "+name+" set A0000=? where A0100=?";
			dao.batchUpdate(updatesql,sortlist);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void sortItem(ContentDAO dao,String name,HashSet hsset){
		ArrayList sortlist = new ArrayList();
		try {
			Iterator  it   =  hsset.iterator(); 
			while(it.hasNext()){
				String A0100 = (String)it.next();
				this.frowset = dao.search("select A0100,I9999 from "+name+" where A0100='"+A0100+"' order by I9999");
				int i=1;
				while(this.frowset.next()){
					ArrayList list = new ArrayList();
					list.add(i+"");
					list.add(this.frowset.getString("A0100"));
					list.add(this.frowset.getString("I9999"));
					sortlist.add(list);
					i++;
				}
				String updatesql = "update "+name+" set I9999=? where A0100=? and I9999=?";
				dao.batchUpdate(updatesql,sortlist); 
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
