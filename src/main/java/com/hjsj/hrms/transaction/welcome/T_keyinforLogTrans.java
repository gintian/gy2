package com.hjsj.hrms.transaction.welcome;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.CreateSequence;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.Date;
/**
 * 公告栏信息管理
 * @author Owner
 *
 */
public class T_keyinforLogTrans extends IBusiness {

	public void execute() throws GeneralException
	{
		String address=(String)this.getFormHM().get("address");//ip地址
		String content=(String)this.getFormHM().get("content");//访问内容
		content=content!=null?content:"";
		content = SafeCode.decode(content);
		//特殊字符还原    jingq  add  2014.07.14
		content = PubFunc.keyWord_reback(content);
		String content_type=(String)this.getFormHM().get("content_type");//访问类型0：公告栏信息 1： 员工薪资表
				
		DbWizard dbWizard =new DbWizard(this.getFrameconn());
		if(!dbWizard.isExistTable("t_keyinfor_log",false))
		{
			return;
		}
		try{	
			if(this.userView.getA0100()==null||this.userView.getA0100().length()<=0)
				return;
			ContentDAO dao=new ContentDAO(this.getFrameconn());		
			Date date =new Date();
			String date_str=PubFunc.FormatDate(date,"yyyy.MM.dd HH:mm:ss");
			java.sql.Date sqldate=DateUtils.getSqlDate(date_str,"yyyy.MM.dd HH:mm:ss");
			String logid=CreateSequence.getUUID();
			RecordVo vo=new RecordVo("t_keyinfor_log");			
			vo.setString("logid", logid);
			vo.setString("nbase", this.userView.getDbname());
			vo.setString("a0100", this.userView.getA0100());
			vo.setString("b0110", this.userView.getUserOrgId());
			vo.setString("e0122", this.userView.getUserDeptId());
			vo.setString("a0101", this.userView.getUserFullName());
			vo.setString("address", address);
			vo.setString("content", content);
			vo.setDate("access_time", sqldate);
			vo.setInt("access_count", 1);
			vo.setString("content_type", content_type);
		    dao.addValueObject(vo);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
