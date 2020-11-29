package com.hjsj.hrms.transaction.train.traincourse;

import com.hjsj.hrms.businessobject.train.TrainClassBo;
import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * <p>Title:培训班</p>
 * <p>Description:培训班报批</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2007-12-13 下午06:07:55</p>
 * @author lilinbing
 * @version 4.0
 */
public class AppealTrans extends IBusiness {

	public void execute() throws GeneralException {
		cat.debug("table name=r31");
		String msg = "true";
		String[] cid = null;
		cat.debug("table name=r31");
		String ids = (String)this.getFormHM().get("ids");
		if(ids!=null&ids.length()>0)
			cid = ids.split(",");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try{
			String namestr="";
			StringBuffer exper = new StringBuffer("");
			TrainClassBo bo = new TrainClassBo(this.frameconn);
			for(int i=0;i<cid.length;i++){
				RecordVo vo = new RecordVo("r31");
				vo.setString("r3101", cid[i]);
				vo = dao.findByPrimaryKey(vo);
				String sp = vo.getString("r3127");
				String r3101 =  vo.getString("r3101");
				String r3130 = vo.getString("r3130");
				r3130=r3130.replaceAll("%26lt;","<").replaceAll("%26gt;",">");
				if("02".equals(sp)){
					exper.append("\n["+r3130+"]"+ResourceFactory.getProperty("train.b_plan.app.submit.approval")+"!");
					continue;
				}else if("03".equals(sp)){
					exper.append("\n\n["+r3130+"]"+ResourceFactory.getProperty("train.b_plan.app.approved")+"!");
					continue;
				}else if("04".equals(sp)){
					exper.append("\n\n["+r3130+"]"+ResourceFactory.getProperty("train.b_plan.app.published")+"!");
					continue;
				}else if("05".equals(sp)){
					exper.append("\n\n["+r3130+"]"+ResourceFactory.getProperty("train.b_plan.app.perform")+"!");
					continue;
				}else if("06".equals(sp)){
					exper.append("\n\n["+r3130+"]"+ResourceFactory.getProperty("train.b_plan.app.end")+"!");
					continue;
				}
				
				if(!bo.checkClassPiv(r3101, this.userView))
				    continue;
				namestr+="'"+r3101+"',";
			}
			if(namestr.trim().length()>0){
				StringBuffer sqlstr = new StringBuffer("");
				sqlstr.append("update r31 set r3127='02' where R3101 in(");
				sqlstr.append(namestr.substring(0,namestr.length()-1));
				sqlstr.append(") ");
				if (!this.userView.isSuper_admin()) {
                    String where = TrainCourseBo.getUnitIdByBusiStrWhere(this.userView);
                    if(where.length()>0)
                        sqlstr.append(where);
                }
				dao.update(sqlstr.toString());
			}
			if(exper.length()>1)
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("train.job.fail")+"\n"+exper.toString()));
		}catch(Exception ex){
			throw GeneralExceptionHandler.Handle(ex);
		}
		this.getFormHM().put("msg", msg);
	}

}
