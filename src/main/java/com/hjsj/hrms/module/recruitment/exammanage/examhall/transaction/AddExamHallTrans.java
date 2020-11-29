package com.hjsj.hrms.module.recruitment.exammanage.examhall.transaction;

import com.hjsj.hrms.module.recruitment.exammanage.examhall.businessobject.ExamHallBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;

import java.util.ArrayList;
/**
 * 
 * 项目名称：hcm7.x 
 * 类名称：AddExamHallTrans 
 * 类描述：添加考场加载类
 * 创建人：sunming 
 * 创建时间：2015-11-30
 * 
 * @version
 */
public class AddExamHallTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		try {
			/**搜索框关键字**/
			String value = (String) this.getFormHM().get("searchtext");
			MorphDynaBean map = (MorphDynaBean) this.getFormHM().get("data");
			
			/**批次编号**/
			String batchId = String.valueOf(map.get("batch_id"));
			/**考场分派中已存在的考场号**/
			String ids="";
			/**考场分派中已存在的考场**/
			String items = (String) this.getFormHM().get("items");
			if(items!=null&&items.length()>0){
				String[] strs = items.substring(1).split(",");
				for(int i=0;i<strs.length;i++){
					ids += ",'"+strs[i].split("`")[0]+"'";
				}
			}
			ExamHallBo bo = new ExamHallBo(this.getFrameconn(),this.userView);
			ArrayList list = bo.toAddExamHall(batchId,ids,value);
			this.getFormHM().put("data", list);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
