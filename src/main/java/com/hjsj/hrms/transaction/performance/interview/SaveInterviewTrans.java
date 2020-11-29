package com.hjsj.hrms.transaction.performance.interview;

import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.util.Date;
import java.util.HashMap;

public class SaveInterviewTrans extends IBusiness{

	public void execute() throws GeneralException {
		PreparedStatement pstmt = null;	
		DbSecurityImpl dbS = new DbSecurityImpl();
		int num = 0;
		try
		{
			HashMap map =this.getFormHM().get("requestPamaHM")==null?null:(HashMap)this.getFormHM().get("requestPamaHM");
			String status = "";
			if(map == null) {
				status = (String)this.getFormHM().get("status");
			}else {
				status = (String)map.get("status");
			}
			String objectid=(String)this.getFormHM().get("objectid");
			String id=(String)this.getFormHM().get("id");
			String interview=(String)this.getFormHM().get("interview");
			interview = PubFunc.keyWord_reback(SafeCode.decode(interview));
			//interview=interview.replaceAll(" ", "#@#");
			String plan_id = (String)this.getFormHM().get("plan_id");
			RecordVo vo = new RecordVo("per_interview");
			ContentDAO dao = new ContentDAO(this.getFrameconn());
		
			if("-1".equals(id))
			{
				IDGenerator idg = new IDGenerator(2, this.getFrameconn());
			    String newid = idg.getId("per_interview.id");
			    id=newid;
			    vo.setString("object_id",objectid);
			    vo.setInt("id", Integer.parseInt(newid));
			    vo.setDate("create_date", new Date());
			    //vo.setString("interview",interview);
			    vo.setString("mainbody_id",this.userView.getA0100());
			    vo.setInt("plan_id",Integer.parseInt(plan_id));
			    vo.setString("status",status);
			    dao.addValueObject(vo);
			    String sql = "update per_interview set interview=? where id="+newid;
			    pstmt = this.getFrameconn().prepareStatement(sql);	
			    switch(Sql_switcher.searchDbServer())
				{
				  case Constant.MSSQL:
				  {
					  pstmt.setString(1, interview);
					  break;
				  }
				  case Constant.ORACEL:
				  {
					  pstmt.setCharacterStream(1,new InputStreamReader(new ByteArrayInputStream(interview.getBytes())), interview.length());
					  break;
				  }
				  case Constant.DB2:
				  {
					  pstmt.setCharacterStream(1,new InputStreamReader(new ByteArrayInputStream(interview.toString().getBytes())), interview.length());
					  break;
				  }
				}
			    // 打开Wallet
				dbS.open(this.getFrameconn(), sql);
			    num = pstmt.executeUpdate();	
			}
			else
			{
				String sql = "update per_interview set interview=?,status='"+status+"' where id="+id;
				pstmt = this.getFrameconn().prepareStatement(sql);	
			    switch(Sql_switcher.searchDbServer())
				{
				  case Constant.MSSQL:
				  {
					  pstmt.setString(1, interview);
					  break;
				  }
				  case Constant.ORACEL:
				  {
					  pstmt.setCharacterStream(1,new InputStreamReader(new ByteArrayInputStream(interview.getBytes())), interview.length());
					  break;
				  }
				  case Constant.DB2:
				  {
					  pstmt.setCharacterStream(1,new InputStreamReader(new ByteArrayInputStream(interview.toString().getBytes())), interview.length());
					  break;
				  }
				}
			    // 打开Wallet
				dbS.open(this.getFrameconn(), sql);
				num = pstmt.executeUpdate();	
				/*vo.setInt("id",Integer.parseInt(id));
				vo=dao.findByPrimaryKey(vo);
				vo.setString("interview",interview);
				dao.updateValueObject(vo);*/
			}
			this.getFormHM().put("id", id);
			this.getFormHM().put("num", num);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		finally
		{
			PubFunc.closeResource(pstmt);
			try {
				// 关闭Wallet
				dbS.close(this.getFrameconn());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}

}
