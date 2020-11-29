package com.hjsj.hrms.transaction.train.resource.myexam;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.Date;

/**
 * <p>
 * Title:SearchMyExam
 * </p>
 * <p>
 * Description:检查时间
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2011-11-29
 * </p>
 * 
 * @author wangzhongjun
 * @version 1.0
 *  
 */
public class CheckTimeMyExam extends IBusiness{

	
	public void execute() throws GeneralException {
		
		String type = (String) this.getFormHM().get("type");
		
		String r5300 = (String) this.getFormHM().get("r5300");
		r5300 = PubFunc.decrypt(SafeCode.decode(r5300));
		
		String r5400 = (String) this.getFormHM().get("r5400");
		r5400 = PubFunc.decrypt(SafeCode.decode(r5400));
		String isPendTask = (String) this.getFormHM().get("isPendTask");
		
		ContentDAO dao = new ContentDAO(this.frameconn);
		
		String flag = "erro";
		
		try {
			long now = new Date().getTime();
			String sql = "select r5405,r5406 from r54 where r5400="+r5400;
			this.frowset = dao.search(sql);
			if (this.frowset.next()) {
				long start = this.frowset.getTimestamp("r5405").getTime();
				long end = this.frowset.getTimestamp("r5406").getTime();
				if (now >= start && now < end) {
					flag = "ok";
				} else if (now < start){
				    flag = "距考试时间还有";
					long hh = (start - now)/(1000 * 60 * 60);
					long mm = ((start - now)%(1000 * 60 * 60))/(1000 * 60);
					long ss = (((start - now)%(1000 * 60 * 60))%(1000 * 60))/1000;
                    if (hh > 0)
                        flag += hh + "小时";

                    if (mm > 0)
                        flag += mm + "分";

                    if (ss > 0)
                        flag += ss + "秒";

                    flag += "，请耐心等待……";
				} else if (now > end) {
				    if("1".equals(isPendTask)) {
				        flag = "ok";
				        this.getFormHM().put("overDate", 1);
				    } else
				        flag = "考试时间已过，不能考试！";
				}
					 
			} else {
				flag = "未找到相应记录！请重试！";
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		this.getFormHM().put("type", type);
		this.getFormHM().put("r5300", SafeCode.encode(PubFunc.encrypt(r5300)));
		this.getFormHM().put("r5400", SafeCode.encode(PubFunc.encrypt(r5400)));
		this.getFormHM().put("biaozhi", SafeCode.encode(flag));
		
	}

}
