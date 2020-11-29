package com.hjsj.hrms.transaction.performance.options;

import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * <p>Title:ListPerDegreeTrans.java</p>
 * <p>Description:绩效评估/考核等级</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-07-15 11:11:11</p>
 * @author JinChunhai
 * @version 1.0
 */

public class ListPerDegreeTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		try
		{	
			Table table = new Table("per_degree");
			DbWizard dbWizard = new DbWizard(this.frameconn);
			DBMetaModel dbmodel = new DBMetaModel(this.getFrameconn());
			if (!dbWizard.isExistField("per_degree", "extPro",false))
			{
			    Field obj = new Field("extPro");
			    obj.setDatatype(DataType.CLOB);
			    obj.setKeyable(false);
			    table.addField(obj);
			    dbWizard.addColumns(table);
			    dbmodel.reloadTableModel("per_degree");
			}				
			
			HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
			String returnflag=(String)hm.get("returnflag");			
			String busitype=(String)hm.get("busitype");
			this.getFormHM().put("returnflag",returnflag);
			this.getFormHM().put("busitype",busitype);
			
		    String planid = (String)hm.get("planid");
		    String degreeId = (String)hm.get("degreeId");
		    hm.remove("degreeId");
		    if(planid!=null)
		    {
				LoadXml loadXml = new LoadXml(this.getFrameconn(), planid);
				Hashtable params = loadXml.getDegreeWhole();
				String degree_id=(String)params.get("GradeClass");
				hm.remove("planid");
				this.getFormHM().put("idSel", degree_id);
				this.getFormHM().put("plan_id", planid);
		    }
	
			ArrayList setlist=this.searchPerDegreeList(busitype);
			if(degreeId==null)
			{
			    if(!setlist.isEmpty())
			    {
					RecordVo vo=(RecordVo)setlist.get(0);
					degreeId = vo.getString("degree_id");				
			    }
			}else if("lastone".equals(degreeId))
			{
				if(setlist.size()>0)
				{
					RecordVo vo=(RecordVo)setlist.get(setlist.size()-1);
				    degreeId = vo.getString("degree_id");
				}			    	
			}
			this.getFormHM().put("degreeId",degreeId);
			this.getFormHM().put("setlist", setlist);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	
	
	public ArrayList searchPerDegreeList(String busitype)throws GeneralException
	{
		ArrayList list=new ArrayList();
		StringBuffer buf=new StringBuffer();
		try
		{						
			buf.append("select degree_id,degreename,degreedesc,topscore,used,flag,domainflag,B0110 from per_degree ");
			
			if(busitype==null || busitype.trim().length()<=0 || "0".equalsIgnoreCase(busitype))
				buf.append(" where flag in(0,1,2,3) ");
			else
				buf.append(" where flag in(4,5) ");			
			
			buf.append(" order by degree_id ");
			
			ContentDAO dao=new ContentDAO(this.getFrameconn());
//			RowSet rset=dao.search(buf.toString());
//			while(rset.next())
//			{
//				LazyDynaBean lazyvo=new LazyDynaBean();
//				lazyvo.set("degreeId", rset.getString("degree_id"));
//				lazyvo.set("degreename", PubFunc.toHtml(rset.getString("degreename")));
//				lazyvo.set("degreedesc", PubFunc.toHtml(rset.getString("degreedesc")));
//				lazyvo.set("topscore", rset.getString("topscore"));
//				lazyvo.set("used", rset.getString("used"));
//				lazyvo.set("flag", rset.getString("flag"));
//				lazyvo.set("domainflag", rset.getString("domainflag"));
//				lazyvo.set("B0110", rset.getString("B0110"));
//				list.add(lazyvo);
			this.frowset = dao.search(buf.toString());
			while (this.frowset.next()) 
			{
				RecordVo vo = new RecordVo("per_degree");
				vo.setString("degree_id", this.frowset.getString("degree_id"));
				vo.setString("degreename", this.frowset.getString("degreename")==null?"":this.frowset.getString("degreename"));
				//vo.setString("degreedesc", PubFunc.toHtml(this.frowset.getString("degreedesc")));
				vo.setString("used", this.frowset.getString("used")==null?"":this.frowset.getString("used"));
				vo.setString("flag", this.frowset.getString("flag")==null?"":this.frowset.getString("flag"));
				vo.setString("domainflag", this.frowset.getString("domainflag")==null?"":this.frowset.getString("domainflag"));
//				vo.setString("B0110", this.frowset.getString("B0110"));
				//haosl 20170419  add 通过vo封装数据时，vo会根据该字段的长度自动截取字符串，通过以下方法避开vo的截取字符串操作。
				HashMap values = vo.getValues();
				values.put("degreedesc", PubFunc.toHtml(this.frowset.getString("degreedesc")));
				vo.setValues(values);
				//haosl	20170419 end 
				list.add(vo);  
				
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return list;
	}
	
}
