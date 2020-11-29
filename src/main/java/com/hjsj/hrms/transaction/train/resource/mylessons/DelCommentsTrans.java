package com.hjsj.hrms.transaction.train.resource.mylessons;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class DelCommentsTrans extends IBusiness {

	public void execute() throws GeneralException {
		
		
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		try {
			
			ContentDAO dao = new ContentDAO(this.frameconn);
			
			if(hm.containsKey("comids")){
				String comids = (String)hm.get("comids");
				hm.remove("comids");
				String[] comidArr = comids.split(","); 
				ArrayList arrayList = new ArrayList();
				for(int i=0;i<comidArr.length;i++){
					RecordVo vo = new RecordVo("tr_course_comments");
					vo.setString("id", PubFunc.decrypt(SafeCode.decode(comidArr[i])));
					arrayList.add(vo);
				}
				
				dao.deleteValueObject(arrayList);
			}
			String id = (String) hm.get("id");
			
			if(id == null || id.length() < 1)
			    return;
			
			id = PubFunc.decrypt(SafeCode.decode(id));
		    StringBuffer buff = new StringBuffer();
		
			
			
			buff.delete(0, buff.length());
			buff.append("delete from tr_course_comments where id=");
			
			buff.append(id);
				
			dao.update(buff.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
}
