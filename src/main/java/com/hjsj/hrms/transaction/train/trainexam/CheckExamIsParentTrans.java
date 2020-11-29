package com.hjsj.hrms.transaction.train.trainexam;

import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.businessobject.train.trainexam.exam.TrainExamPlanBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;

public class CheckExamIsParentTrans extends IBusiness {
	
	/* 培训课程 检测代码项是否为上级
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String isParent = "no";
		if(!this.userView.isSuper_admin()){
			String codeitemid = "";
			String id = (String)this.getFormHM().get("id");
			if(id!=null&&id.length()>0)
				codeitemid=getB01110(id);
			codeitemid=codeitemid==null?"":codeitemid;
			String codearr[] =codeitemid.split(",");
			for (int i = 0; i < codearr.length; i++) {
				codeitemid=codearr[i];
				TrainCourseBo tbo = new TrainCourseBo(this.userView);
				if(tbo.isUserParent(/*tmpb0110*/codeitemid)==2){
					isParent = "yes";
					break;
				}
			}
		}
		this.getFormHM().put("isParent", isParent);
	}
	
    private String getB01110(String id) {

        String codes = "";
        try {
            if (id.endsWith(","))
                id = id.substring(0, id.length() - 1);
            ArrayList list = TrainExamPlanBo.getList(id);
            if(list == null || list.size() < 1)
                return codes;
                
            for (int i = 0; i < list.size(); i++) {
                String value = (String) list.get(i);
                String sql = "select b0110 from r53 where r5300 in (" + value + ")";
                ContentDAO dao = new ContentDAO(this.frameconn);

                this.frowset = dao.search(sql);
                while (this.frowset.next()) {
                    codes += this.frowset.getString("b0110") + ",";
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return codes;
    }
}
