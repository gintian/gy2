package com.hjsj.hrms.transaction.train.trainexam.paper.preview;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;

/**
 * 校验试卷
 * @author LiWeichao
 *
 */
public class VerifyPapersTrans extends IBusiness {

	public void execute() throws GeneralException {
		String flag = (String)this.getFormHM().get("flag");//null或空=试卷校验    1=手工选题类型预览试卷时的校验 2=自动选题类型预览试卷时的校验
		String r5300 = (String)this.getFormHM().get("r5300");//试卷id
		r5300 = PubFunc.decrypt(SafeCode.decode(r5300));
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String sql="select sum(r5213) s from tr_exam_paper t,r52 r where r.r5200=t.r5200 and r5300="+r5300;
		float sum = 0f;
		try {
			this.frowset = dao.search(sql);
			if(this.frowset.next())
				sum = this.frowset.getFloat("s");
			

			if("2".equals(flag)){//自动组卷中引用手工组卷提示
				flag="1";
				String strsum=(String)this.getFormHM().get("score");
				sum = strsum==null||strsum.length()<1?0:Float.parseFloat(strsum);
			}
			
			sql="select r5304 from r53 where r5300="+r5300;
			this.frowset = dao.search(sql);
			if(this.frowset.next()){
				float f = this.frowset.getFloat("r5304");
				if(f==sum)
					this.getFormHM().put("flag", "ok");
				else{
					this.getFormHM().put("flag", "no");
					
					if("1".equals(flag))
						this.getFormHM().put("mess", SafeCode.encode("所选试题总分"+sum+"不等于试卷已定义分值("+f+"),是否继续？"));
					else
						this.getFormHM().put("mess", SafeCode.encode("试卷满分为："+f+"\r\n当前试题总分为："+sum));
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}