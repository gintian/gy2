package com.hjsj.hrms.transaction.dtgh.party;

import com.hjsj.hrms.valueobject.common.FieldItemView;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;

public class AddorEditPartySubInfoTrans extends IBusiness {

	public void execute() throws GeneralException {
		
		String fieldsetid = (String)this.getFormHM().get("fieldsetid");
		if(fieldsetid==null||fieldsetid.length()<=0)
			return;
		String type=(String)this.getFormHM().get("subtype");
		String codeitemid = (String)this.getFormHM().get("codeitemid");
		ArrayList infolist = DataDictionary.getFieldList(fieldsetid, 1);
		ArrayList infofieldlist = new ArrayList();
		String i9999 = (String)this.getFormHM().get("i9999");
		 RecordVo vo = new RecordVo(fieldsetid.toLowerCase());
		 if("edit".equals(type)){
			 ContentDAO dao = new ContentDAO(this.frameconn);
			 vo.setString((fieldsetid.substring(0,1)+"0100").toLowerCase(), codeitemid);
			 vo.setString("i9999", i9999);
			 try {
				vo = dao.findByPrimaryKey(vo);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 }
		for(int i=0;i<infolist.size();i++){
			 FieldItem fieldItem=(FieldItem)infolist.get(i);
			 FieldItemView fieldItemView=new FieldItemView();
				fieldItemView.setAuditingFormula(fieldItem.getAuditingFormula());
				fieldItemView.setAuditingInformation(fieldItem.getAuditingInformation());
				fieldItemView.setCodesetid(fieldItem.getCodesetid());
				fieldItemView.setDecimalwidth(fieldItem.getDecimalwidth());
				fieldItemView.setDisplayid(fieldItem.getDisplayid());
				fieldItemView.setDisplaywidth(fieldItem.getDisplaywidth());
				fieldItemView.setExplain(fieldItem.getExplain());
				fieldItemView.setFieldsetid(fieldItem.getFieldsetid());
				fieldItemView.setItemdesc(fieldItem.getItemdesc());
				fieldItemView.setItemid(fieldItem.getItemid().toUpperCase());
				fieldItemView.setItemlength(fieldItem.getItemlength());
				fieldItemView.setItemtype(fieldItem.getItemtype());
				fieldItemView.setModuleflag(fieldItem.getModuleflag());
				fieldItemView.setState(fieldItem.getState());
				fieldItemView.setUseflag(fieldItem.getUseflag());
				fieldItemView.setPriv_status(fieldItem.getPriv_status());
				fieldItemView.setFillable(fieldItem.isFillable());
				fieldItemView.setInputtype(fieldItem.getInputtype());
             //在struts用来表示换行的变量
			    fieldItemView.setRowflag(String.valueOf(infolist.size()+1));
			    if("edit".equals(type)){
			    	if("D".equals(fieldItemView.getItemtype())){
			    		fieldItemView.setValue(vo.getString((fieldItemView.getItemid()).toLowerCase()).replaceAll("-", "\\."));
			    	}else{
				    	fieldItemView.setValue(vo.getString((fieldItemView.getItemid()).toLowerCase()));
				    	if(!"0".equals(fieldItemView.getCodesetid())){
				    		fieldItemView.setViewvalue(AdminCode.getCodeName(fieldItemView.getCodesetid(), vo.getString((fieldItemView.getItemid()).toLowerCase())));
				    	}
			    	}
			    }
			    infofieldlist.add(fieldItemView);
		}
		this.getFormHM().put("infofieldlist", infofieldlist);
	}
}