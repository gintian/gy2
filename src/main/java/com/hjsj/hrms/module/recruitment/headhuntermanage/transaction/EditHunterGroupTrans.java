package com.hjsj.hrms.module.recruitment.headhuntermanage.transaction;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;

public class EditHunterGroupTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		 try {
			 
			 String subType = (String)this.getFormHM().get("subType");
			 ArrayList columns = (ArrayList)this.getFormHM().get("groupcolumns");
			 ArrayList newColumn = new ArrayList();
			 //新增  添加主键
			 if("insert".equals(subType)){
				 for(int i=0;i<columns.size();i++){
						ColumnsInfo c = ((ColumnsInfo)columns.get(i)).clone();
						if(c.getColumnId() != null && "z6000".equals(c.getColumnId().toLowerCase()))
							c.setKey(true);
						newColumn.add(c);
				 }
				 
				 this.formHM.put("editColumns", newColumn);
				 return;
			 }
			 
			 
			 //修改功能
	          String hunterid = (String)this.getFormHM().get("huntergroupid");
	          hunterid = PubFunc.decrypt(hunterid);
	          RecordVo vo = new RecordVo("z60");
	          vo.setObject("z6000", hunterid);
	 		
       
			vo = new ContentDAO(frameconn).findByPrimaryKey(vo);
			
			for(int i=0;i<columns.size();i++){
				ColumnsInfo c = ((ColumnsInfo)columns.get(i)).clone();
				newColumn.add(c);
				if(!vo.hasAttribute(c.getColumnId()))
					continue;
				if("z6000".equals(c.getColumnId().toLowerCase()))
					c.setKey(true);
				if("0".equals(c.getCodesetId())){
					Object obj = vo.getObject(c.getColumnId());
					c.setDefaultValue(obj==null?"":obj.toString());
				}else{
					String codesetid = c.getCodesetId();
					String codeid = vo.getString(c.getColumnId());
					String name = AdminCode.getCodeName(codesetid,codeid);
					c.setDefaultValue(codeid+"`"+name);
				}
				if("A".equals(c.getColumnType())){
					c.setDefaultValue(c.getDefaultValue().replaceAll("<", "＜"));
					c.setDefaultValue(c.getDefaultValue().replaceAll(">", "＞"));
				}
			}
			
			this.formHM.put("editColumns", newColumn);
			//this.formHM.put("subType", "update");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

}
