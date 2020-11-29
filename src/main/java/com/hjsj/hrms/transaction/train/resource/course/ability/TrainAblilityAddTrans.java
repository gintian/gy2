package com.hjsj.hrms.transaction.train.resource.course.ability;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * @Title:        TrainAblilityAddTrans.java 
 * @Description:  培训课程关联素质指标
 * @Company:      hjsj     
 * @Create time:  2014-5-22 下午05:12:00 
 * @author:       chenxg
 * @version:      6.x
 */
public class TrainAblilityAddTrans extends IBusiness {

	public void execute() throws GeneralException {
		String flag = "false";
		ArrayList list = new ArrayList();
		String lessonid = (String) this.getFormHM().get("lessonids");
		String pointid =(String) this.getFormHM().get("pointids");
		int msg = 0;
		
		if(pointid.indexOf(",")==0)
		    pointid = pointid.substring(1);
		try{
		    String[] lessonids = PubFunc.keyWord_reback(lessonid).split(";");
			String[] pointids = pointid.split(",");
			ContentDAO dao = new ContentDAO(this.frameconn);
	            
            for (int i = 0; i < lessonids.length; i++) {
                String[] lids = PubFunc.keyWord_reback((String) lessonids[i]).split(",");
                for (int m = 0; m < lids.length; m++) {
                    String lid = (String) lids[m];
                    lid = PubFunc.decrypt(SafeCode.decode(lid));
                    HashMap map = new HashMap();
                    String sql = "select point_id from per_point_course where r5000='" + lid + "'";
                    this.frowset = dao.search(sql);
                    while (this.frowset.next()) {
                        map.put(this.frowset.getString("point_id"), "");
                    }

                    for (int k = 0; k < pointids.length; k++) {
                        ArrayList valuelist = new ArrayList();
                        if (pointids[k] != null && pointids[k].length() > 0) {
                            if (!map.containsKey(pointids[k])) {
                                valuelist.add(lid);
                                valuelist.add(pointids[k]);
                                list.add(valuelist);
                            } else
                                msg++;

                        }
                    }
                }
            }
			
			int[] i = dao.batchInsert("insert into per_point_course (r5000,point_id) values (?,?)", list);
			if(i.length>0 || msg > 0)
				flag = "ture";
			
		}catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
		this.getFormHM().put("flag", flag);
	}
}