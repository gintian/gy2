package com.hjsj.hrms.module.recruitment.position.transaction;

import com.hjsj.hrms.module.recruitment.position.businessobject.PositionBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.axis.utils.StringUtils;

/******
 * 删除职位时对该职位下的考生进行查询
 * <p>Title: SearchExamineeTrans </p>
 * <p>Description: </p>
 * <p>Company: hjsj</p>
 * <p>create time: 2015-12-1 上午10:23:02</p>
 * @author xiexd
 * @version 1.0
 */
public class SearchExamineeTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {

		String z0301 = (String) this.getFormHM().get("z0301s");//获取需要删除的职位编号
		String []z0301s = z0301.split(",");
		PositionBo bo = new PositionBo(this.getFrameconn(),new ContentDAO(this.getFrameconn()), this.userView);
		
		String msg = "";//提示信息
		int num = 0;//含有考生的职位数量
		for(int i=0;i<z0301s.length;i++)
		{
//			String positionName = bo.getPositionExaminee(PubFunc.decrypt(z0301s[i]));
			String positionName = bo.getPositionCandidate(PubFunc.decrypt(z0301s[i]));
			if(!StringUtils.isEmpty(positionName))
				msg = "职位“"+positionName+"”下已经有应聘人员数据，不允许删除！";
		}
		/*if(msg.length()>0)
		{
			msg = msg.substring(0, msg.length()-1);
		}
		
		if(num>=3)
		{
			msg = msg+"等"+num+"个职位下有应聘者已 安排了考试，<br>是否继续删除职位？（将同时删除考生信息）";
		}else if(num==0)
		{
			msg = "";
		}else{
			msg = msg+"该"+num+"个职位下有应聘者已 安排了考试，<br>是否继续删除职位？（将同时删除考生信息）";
		}*/
		this.getFormHM().put("z0301s", z0301);
		this.getFormHM().put("msg", msg);
	}

}
