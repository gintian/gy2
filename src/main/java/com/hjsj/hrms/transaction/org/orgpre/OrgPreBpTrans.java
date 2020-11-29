package com.hjsj.hrms.transaction.org.orgpre;

import com.hjsj.hrms.businessobject.org.orgpre.OrgPreBo;
import com.hjsj.hrms.businessobject.pos.posparameter.PosparameXML;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
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
public class OrgPreBpTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		String name=(String)hm.get("position_set_table");
		cat.debug("table name="+name);
		ArrayList list=(ArrayList)hm.get("position_set_record");
		PosparameXML pos = new PosparameXML(this.frameconn); 
		String sp_flag = pos.getValue(PosparameXML.AMOUNTS,"sp_flag");
		FieldSet fieldset = DataDictionary.getFieldSetVo(name);
		try{
			String namestr="";
			String b0110="";
			StringBuffer exper = new StringBuffer("");
			ArrayList valuelist = new ArrayList();
			OrgPreBo orgprebo = new OrgPreBo(this.frameconn,this.userView);
			if(list.size()<1)
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("workdiary.message.select.drafting.dismissed")+"!"));
			for(int i=0;i<list.size();i++){
				RecordVo vo=(RecordVo)list.get(i);
				String sp = vo.getString(sp_flag.toLowerCase());
				sp=sp!=null&&sp.trim().length()>0?sp:"01";
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
					namestr =orgprebo.orgCodeName(b0110);
				if("02".equals(sp)){
					exper.append("\n\n["+namestr+"]"+ResourceFactory.getProperty("train.b_plan.app.submit.approval")+"!");
					continue;
				}else if("03".equals(sp)){
					exper.append("\n\n["+namestr+"]"+ResourceFactory.getProperty("train.b_plan.app.approved")+"!");
					continue;
				}
				ArrayList lista =  new ArrayList();
				lista.add(b0110);
				if(!fieldset.isMainset()){
					lista.add(vo.getString("i9999"));
				}
				valuelist.add(lista);
			}
			String updatestr = "";
			if(fieldset.isMainset()){
				updatestr = "update "+name.toUpperCase()+" set "+sp_flag+"='02' ";
				if("B".equalsIgnoreCase(name.substring(0,1))){
					updatestr+="where B0110=?";
				}else if("K".equalsIgnoreCase(name.substring(0,1))){
					updatestr+="where E01A0=?";
				}
			}else{
				updatestr = "update "+name+" set "+sp_flag+"='02' ";
				if("B".equalsIgnoreCase(name.substring(0,1))){
					updatestr+="where B0110=?";
					updatestr+=" and I9999=?";
				}else if("K".equalsIgnoreCase(name.substring(0,1))){
					updatestr+="where E01A0=?";
					updatestr+=" and I9999=?";
				}
				
			}
			if(valuelist.size()>0){
				ContentDAO dao=new ContentDAO(this.getFrameconn());
				dao.batchUpdate(updatestr,valuelist);
			}
			if(exper.length()>1)
				throw GeneralExceptionHandler.Handle(new Exception(exper.toString()));
		}catch(Exception ex){
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

}
