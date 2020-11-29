package com.hjsj.hrms.transaction.welcome;

import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 保存公告回复信息
 * <p>Title:SavePronunciamentoRevertTrans.java</p>
 * <p>Description>:SavePronunciamentoRevertTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Jan 7, 2010 4:20:20 PM</p>
 * <p>@version: 4.0</p>
 * <p>@author: s.xin
 */
public class SavePronunciamentoRevertTrans  extends IBusiness {

	public void execute() throws GeneralException
	{
		String content_type=(String)this.getFormHM().get("content_type");//访问类型0：公告栏信息 1： 员工薪资表
		String opinion=(String)this.getFormHM().get("opinion");
		String content=(String)this.getFormHM().get("content");//访问内容
		content=content!=null?content:"";
		content = SafeCode.decode(content);
		DbWizard dbWizard =new DbWizard(this.getFrameconn());
		String flag="no";
		if(!dbWizard.isExistTable("t_keyinfor_log",false))
		{
			this.getFormHM().put("flag", flag);
			this.getFormHM().put("mess", "操作日志表不存在，请对数据库进行升级后再进行此操作！");
			return;
		}
		if(this.userView.getA0100()==null||this.userView.getA0100().length()<=0)
		{
			this.getFormHM().put("mess", "您没有关联账号人员，请关联账号人员后再进行此操作！");
			this.getFormHM().put("flag", flag);
			return;
		}
		
		StringBuffer sql=new StringBuffer();
		sql.append("select logid from t_keyinfor_log");
		sql.append(" where nbase='"+this.userView.getDbname()+"'");
		sql.append(" and a0100='"+this.userView.getA0100()+"' and content='"+content+"'");
		sql.append(" and content_type='"+content_type+"'");
		sql.append(" order by access_time desc");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String logid="";
		try
		{
			this.frowset=dao.search(sql.toString());
			if(this.frowset.next())
			{
				logid=this.frowset.getString("logid");
			}
			if(logid!=null&&logid.length()>0)
			{
				RecordVo vo=new RecordVo("t_keyinfor_log");			
				vo.setString("logid", logid);
				vo=dao.findByPrimaryKey(vo);
				if(vo!=null)
				{
					vo.setString("opinion", opinion);
					dao.updateValueObject(vo);
					flag="ok";
				}
			}
		}catch(Exception e)
		{
			e.printStackTrace();
			
		}
		this.getFormHM().put("flag", flag);
		this.getFormHM().put("mess", "");
	}

}
