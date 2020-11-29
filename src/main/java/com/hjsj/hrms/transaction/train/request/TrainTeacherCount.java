package com.hjsj.hrms.transaction.train.request;

import com.hjsj.hrms.businessobject.train.TrainClassBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;

/**
 * <p>
 * Title:培训班教师数（内部）
 * </p>
 * <p>
 * Description:培训班教师数（内部）
 * </p>
 * 
 * @author zhaoxj
 * @version 1.0
 */
public class TrainTeacherCount extends IBusiness {

	public void execute() throws GeneralException {

	    try {
	        String classid = (String) this.getFormHM().get("classid");
	        TrainClassBo bo = new TrainClassBo(this.frameconn);
	        if(!bo.checkClassPiv(classid, this.userView)){
	            this.getFormHM().put("tcount", "0");
                return;
	        }
    		
    		//检查是否有内部教师关联指标nbase
    		DbWizard dbWizard = new DbWizard(this.getFrameconn());
    		boolean haveInnerTeacherFlag = dbWizard.isExistField("R04", "nbase", false);
    		if (!haveInnerTeacherFlag) {
    			this.getFormHM().put("tcount", "0");
    			return;
    		}
    		
    		//有关联的，检查当前培训班中是否有内部教师

			ContentDAO dao = new ContentDAO(this.getFrameconn());
			
			StringBuffer sql = new StringBuffer("SELECT count(R4101) AS tcount FROM R41");
			sql.append(" WHERE R4103='" + classid + "'");
			sql.append(" AND EXISTS(SELECT 1 FROM R04 WHERE R0401=R41.R4106");
			sql.append(" AND " + Sql_switcher.isnull("nbase", "'x'") + "<>'x'");
			sql.append(" AND " + Sql_switcher.isnull("A0100", "'x'") + "<>'x'");
			if (Constant.MSSQL == Sql_switcher.searchDbServer()) {
				sql.append(" AND nbase<>'' AND A0100<>''");
			}
			sql.append(") ");
			
			this.frowset = dao.search(sql.toString());
			if (this.frowset.next()) {
				int tcount = this.frowset.getInt("tcount");
				this.getFormHM().put("tcount", String.valueOf(tcount));
			}
		} catch (SQLException e) {
		    e.printStackTrace();
		}

	}

}
