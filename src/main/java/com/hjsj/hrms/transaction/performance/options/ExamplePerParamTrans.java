package com.hjsj.hrms.transaction.performance.options;

import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * <p>Title:ExamplePerParamTrans.java</p>
 * <p>Description:提取固定评语模版</p>
 * <p>Company:hjsj</p>
 * <p>create time:2010-06-15 11:28:36</p>
 * @author JinChunhai
 * @version 5.0
 */

public class ExamplePerParamTrans extends IBusiness
{

    public void execute() throws GeneralException
    {

		HashMap hm = this.getFormHM();
		// try
		// {
		// PerParamBo bo=new PerParamBo(this.getFrameconn());
		// RecordVo recordVo=bo.getPlanVo("1");
		// String content=(String)recordVo.getString("content");
		// hm.put("content",SafeCode.encode(content));
		//			
		// }
		// catch(Exception ex)
		// {
		// ex.printStackTrace();
		// throw GeneralExceptionHandler.Handle(ex);
		// }
		StringBuffer content = new StringBuffer("               [考核对象]同志[考核时间][考核计划]考评情况\n");
		content.append("一、测评情况\n");
		content.append("  [考核对象]同志考评综合得分[综合得分]，等级为[等级]，在市高级法院[总数]位班子副职中排名第[名次]位。[评价统计]\n");
		content.append("  在各考评群体中，[分类得分] ll.\n");
		content.append("二、考评情况分析\n");
		content.append("  分析[指标总数]个单项指标的得分情况，[考核对象]同志在[高分指标]等方面得分较高，在[低分指标]等方面得分较低。\n"); 
		content.append("三、综合评价\n");
		hm.put("content",SafeCode.encode(content.toString()));
		
    }
    
}
