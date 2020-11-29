package com.hjsj.hrms.transaction.performance.objectiveManage.objectiveCard;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.io.Reader;
import java.io.StringReader;
import java.sql.PreparedStatement;
import java.util.HashMap;

public class OptGoalSummaryTrans extends IBusiness {


	public void execute() throws GeneralException {
		DbSecurityImpl dbS = new DbSecurityImpl();
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String opt=(String)hm.get("b_opt");  // opt1:批准 opt2:驳回
			String plan_id=(String)this.getFormHM().get("planid");
			String object_id=(String)this.getFormHM().get("object_id");
			
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			String Article_type="2"; //绩效报告
			
			
			RecordVo plan_vo=new RecordVo("per_plan");
			plan_vo.setInt("plan_id",Integer.parseInt(plan_id));
			plan_vo=dao.findByPrimaryKey(plan_vo);
			
			String a_objectid=object_id;
			if(plan_vo.getInt("object_type")==1||plan_vo.getInt("object_type")==3||plan_vo.getInt("object_type")==4)
			{
				RowSet rowSet=dao.search("select * from per_mainbody where plan_id="+plan_id+" and object_id='"+object_id+"' and body_id=-1");
				if(rowSet.next())
				{
					a_objectid=rowSet.getString("mainbody_id");	
				}
				
			}
			
			if("opt1".equals(opt))
			{
				
				
				RowSet rowSet=dao.search("select count(*) from  per_article where plan_id="+plan_id+" and fileflag=1 and Article_type="+Article_type+" and  a0100='"+a_objectid+"'");
				int count=0;
				if(rowSet.next())
					count=rowSet.getInt(1);
				if(count==0)
				{
					insertPerArticleRecord(plan_id,Integer.parseInt(Article_type),1,a_objectid);
				}
				dao.update("update per_article set state=2,description='' where plan_id="+plan_id+" and fileflag=1 and Article_type="+Article_type+" and  a0100='"+a_objectid+"'");	
			}
			else if("opt2".equals(opt))  //驳回
			{
				
				
				String rejectCause=(String)this.getFormHM().get("rejectCause");
				String sql = "update per_article set state=3,description=? where plan_id="+plan_id+" and fileflag=1 and Article_type="+Article_type+" and  a0100='"+a_objectid+"'";
				try(
				PreparedStatement pt=this.getFrameconn().prepareStatement(sql);
				) {
					if (Sql_switcher.searchDbServer() == Constant.ORACEL) {
						Reader clobReader = new StringReader(rejectCause);
						pt.setCharacterStream(1, clobReader, rejectCause.length());
					} else
						pt.setString(1, rejectCause);
					// 打开Wallet
					dbS.open(this.getFrameconn(), sql);
					pt.execute();
				}
			}
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try {
				// 关闭Wallet
				dbS.close(this.getFrameconn());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	

//	新建个人目标记录
	private int insertPerArticleRecord(String planid,int article_type,int fileflag,String object_id)
	{
		int article_id=0;
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			DBMetaModel dbmodel=new DBMetaModel(this.getFrameconn());
			dbmodel.reloadTableModel("per_article");
			RecordVo avo=new RecordVo("per_article");
			article_id= DbNameBo.getPrimaryKey("per_article","article_id",this.frameconn);
			avo.setInt("article_id", article_id);
			avo.setInt("plan_id",Integer.parseInt(planid));
			String b0110="";String e0122="";String e01a1="";String a0101="";
			this.frowset=dao.search("select b0110,e0122,e01a1,a0101 from UsrA01 where a0100='"+object_id+"'");
			if(this.frowset.next())
			{
				b0110=this.frowset.getString("b0110")!=null?this.frowset.getString("b0110"):"";
				e0122=this.frowset.getString("e0122")!=null?this.frowset.getString("e0122"):"";
				e01a1=this.frowset.getString("e01a1")!=null?this.frowset.getString("e01a1"):"";
				a0101=this.frowset.getString("a0101")!=null?this.frowset.getString("a0101"):"";
			}
			avo.setString("b0110",b0110);
			avo.setString("e0122", e0122);
			avo.setString("e01a1", e01a1);
			avo.setString("nbase","Usr");
			avo.setString("a0100",object_id);
			avo.setString("a0101",a0101);
			avo.setInt("article_type", article_type);
			avo.setInt("fileflag",fileflag);
			avo.setInt("state",0);
			dao.addValueObject(avo);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return article_id;
	}
	

}
