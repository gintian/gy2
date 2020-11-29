package com.hjsj.hrms.transaction.train.resource.course.pos;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

public class AddPosCourseTrans extends IBusiness {

	public void execute() throws GeneralException {
		String state=(String)this.getFormHM().get("state");
		String a_code=(String)this.getFormHM().get("a_code");
    	String codeitemid=a_code.substring(2);
		ArrayList posCourselist=(ArrayList)this.getFormHM().get("selectedList");
		ContentDAO dao = new ContentDAO(this.frameconn);
		RecordVo vo = null;
		try {
			for(int i=0; i<posCourselist.size();i++){
				LazyDynaBean dynaBean = (LazyDynaBean) posCourselist.get(i);
				vo = new RecordVo("tr_job_course");
				vo.setString("job_id", codeitemid);
				vo.setString("r5000", (String)dynaBean.get("r5000"));
				vo.setInt("state", Integer.parseInt(state));
//				this.frowset = dao.search("select 1 from tr_job_course where job_id='"+codeitemid+"' and r5000='"+(String)dynaBean.get("r5000")+"' and state="+state);
//				if(this.frowset.next()){
//					//System.out.println("--update--");
//					dao.updateValueObject(vo);
//				}else{
//					//System.out.println("--add--");
					dao.addValueObject(vo);
//				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
