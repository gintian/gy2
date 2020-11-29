package com.hjsj.hrms.transaction.browse;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
/**
 * 员工浏览时调用参数
 * <p>Title:BrowseConfigParamTrans.java</p>
 * <p>Description>:BrowseConfigParamTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Jan 18, 2010 4:18:54 PM</p>
 * <p>@version: 4.0</p>
 * <p>@author: s.xin
 */
public class BrowseConfigParamTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		
		String clientName=SystemConfig.getPropertyValue("clientName");
		if(clientName==null)
			clientName="";//判断软件对应的是哪个公司企业
		//zfw政法委
		this.getFormHM().put("clientName", clientName);
		if("zfw".equalsIgnoreCase(clientName))
		{
			this.getFormHM().put("task_card_attach", "true");//显示任务说明书
			String caution_field=SystemConfig.getPropertyValue("caution_field");////警示指标 
			String caution_codeitems=SystemConfig.getPropertyValue("caution_codeitems");//指标代码项  01,02,03(以逗号隔开)
			String caution_colors=SystemConfig.getPropertyValue("caution_colors");//代码项对应的颜色 #000000,#002233,#cceeww(以逗号隔开)
			if(caution_field!=null&&caution_field.length()>0&&caution_codeitems!=null&&caution_codeitems.length()>0&&caution_colors!=null&&caution_colors.length()>0)
			   this.getFormHM().put("caution_color", "true");//颜色警示
			else
				this.getFormHM().put("caution_color", "false");//颜色警示	   
		}else
		{
			this.getFormHM().put("task_card_attach", "false");			
			this.getFormHM().put("caution_color", "false");
		}
		/****************是否显示岗位附件*************/
		String sql="select str_value from constant where constant='PS_CARD_ATTACH'";
		/*RecordVo vo = new RecordVo("CONSTANT");
		vo.setString("constant", "PS_CARD_ATTACH");*/
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String value="";
		try {
			this.frowset=dao.search(sql);
			if(this.frowset.next())
			{
				value=this.frowset.getString("str_value");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 if(value==null|| "".equals(value))
			 value="false";
	    this.getFormHM().put("ps_card_attach", value);
	    /****************结束*************/
	}

}
