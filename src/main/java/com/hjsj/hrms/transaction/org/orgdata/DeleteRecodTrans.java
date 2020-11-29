package com.hjsj.hrms.transaction.org.orgdata;

import com.hjsj.hrms.businessobject.pos.posparameter.PosparameXML;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:${date}:${time}</p>
 * @author lilinbing
 * @version 1.0
 * 
 */

public class DeleteRecodTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		String name=(String)hm.get("data_table_table");
		cat.debug("table name="+name);
		ArrayList list=(ArrayList)hm.get("data_table_record");	
		
		String pri = this.userView.analyseTablePriv(name);
		if("0".equals(pri))
			throw new GeneralException("没有删除此子集中记录的权限!");
		
		PosparameXML pos = new PosparameXML(this.frameconn); 
		String sp_flag = pos.getValue(PosparameXML.AMOUNTS,"sp_flag");
		
		try{
			FieldSet fieldset = DataDictionary.getFieldSetVo(name);
			String namestr="";
			String b0110="";
			StringBuffer exper = new StringBuffer("");
			ArrayList itemlist = new ArrayList();
			ArrayList valuelist = new ArrayList();
			for(int i=0;i<list.size();i++){
				RecordVo vo=(RecordVo)list.get(i);
				String sp = vo.getString(sp_flag.toLowerCase());
				if("B".equalsIgnoreCase(name.substring(0,1)))
					b0110=vo.getString("b0110");
				else if("K".equalsIgnoreCase(name.substring(0,1)))
					b0110=vo.getString("e01a1");
				namestr = AdminCode.getCodeName(b0110,"UN");
				if(namestr==null||namestr.trim().length()<1)
					namestr = AdminCode.getCodeName(b0110,"UM");
				if(namestr==null||namestr.trim().length()<1)
					namestr = AdminCode.getCodeName(b0110,"@K");
				if(namestr==null||namestr.trim().length()<1)
					namestr =b0110;
				if("02".equals(sp)){
					exper.append("\n\n["+namestr+"]"+ResourceFactory.getProperty("train.b_plan.app.submit.approval")+"!");
					continue;
				}else if("03".equals(sp)){
					exper.append("\n\n["+namestr+"]"+ResourceFactory.getProperty("train.b_plan.app.approved")+"!");
					continue;
				}
				ArrayList rlist=new ArrayList();
				if("B".equalsIgnoreCase(name.substring(0,1)))
					rlist.add(vo.getString("b0110"));
				else if("K".equalsIgnoreCase(name.substring(0,1)))
					rlist.add(vo.getString("e01a1"));
				
				itemlist.add(rlist);
				valuelist.add(vo);
			}
			if(valuelist.size()>0){
				ContentDAO dao=new ContentDAO(this.getFrameconn());	
				dao.deleteValueObject(valuelist);
				
				if(fieldset!=null&&fieldset.isMainset()){
					ArrayList fieldlist = new ArrayList();
					if("B".equalsIgnoreCase(name.substring(0,1)))
						fieldlist = DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.UNIT_FIELD_SET);
					else if("K".equalsIgnoreCase(name.substring(0,1)))
						fieldlist = DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.POS_FIELD_SET);
					for(int i=0;i<fieldlist.size();i++){
						FieldSet setitem = (FieldSet)fieldlist.get(i);
						if("B01".equalsIgnoreCase(setitem.getFieldsetid())|| "K01".equalsIgnoreCase(setitem.getFieldsetid()))
							continue;
						String tablename = setitem.getFieldsetid();
						String delall = "";
						if("B".equalsIgnoreCase(name.substring(0,1)))
							delall = "delete from "+tablename+" where B0110=?";
						else if("K".equalsIgnoreCase(name.substring(0,1)))
							delall = "delete from "+tablename+" where E01A1=?";
						dao.batchUpdate(delall,itemlist);
					}
				}
			}
			if(exper.length()>1)
				throw GeneralExceptionHandler.Handle(new Exception(exper.toString()));
		}catch(Exception ex){
			throw GeneralExceptionHandler.Handle(ex);
		}

	}

}
