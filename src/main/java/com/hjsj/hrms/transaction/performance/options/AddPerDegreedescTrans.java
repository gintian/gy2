package com.hjsj.hrms.transaction.performance.options;

import com.hjsj.hrms.businessobject.performance.options.PerDegreedescBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * <p>Title:AddPerDegreedescTrans.java</p>
 * <p>Description>:保存新增项目等级分类</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Jan 17, 2011 12:15:35 AM</p>
 * <p>@version: 5.0</p>
 * <p>@author: JinChunhai
 */

public class AddPerDegreedescTrans extends IBusiness
{
    private static final long serialVersionUID = 1L;

    public void execute() throws GeneralException
    {
		RecordVo votemp = (RecordVo) this.getFormHM().get("perdegreedescvo");
		String id = votemp.getString("id");
		String degreeId = (String) this.getFormHM().get("degreeId");
		int degree_id=Integer.parseInt(degreeId);
		String itemname = votemp.getString("itemname");
		String topscore = "".equals(votemp.getString("topscore"))?"0":votemp.getString("topscore");
		String bottomscore = "".equals(votemp.getString("bottomscore"))?"0":votemp.getString("bottomscore");
		String itemdesc = votemp.getString("itemdesc");
		String percentvalue = votemp.getString("percentvalue");
		String strict = "".equals(votemp.getString("strict"))?"0":votemp.getString("strict");
		String flag = votemp.getString("flag");
		String xishu = "".equals(votemp.getString("xishu"))?"0":votemp.getString("xishu");
	
		String degreeFlag = (String)this.getFormHM().get("flag");
		if("0".equals(degreeFlag) || "2".equals(degreeFlag))
		{
			strict = Double.toString(Double.parseDouble(strict)/100);
			flag="0";
		}else if("1".equals(degreeFlag) || "3".equals(degreeFlag))
			flag="1";
		else if("4".equals(degreeFlag))
			flag="4";
		else if("5".equals(degreeFlag))
			flag="5";
		
		String info = (String) this.getFormHM().get("info");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
	
		try
		{	
		    RecordVo vo = new RecordVo("per_degreedesc");
		    if ("".equals(id) || id == null)
		    {
				IDGenerator idg = new IDGenerator(2, this.getFrameconn());
				id = idg.getId("per_degreedesc.id");
				int per_id=Integer.parseInt(id);
				vo.setInt("id", per_id);
				vo.setInt("degree_id", degree_id);
				vo.setString("itemname", itemname);
				vo.setDouble("topscore", Double.parseDouble(topscore));
				vo.setDouble("bottomscore", Double.parseDouble(bottomscore));
				vo.setString("itemdesc", itemdesc);
				
				if(percentvalue!=null && percentvalue.trim().length()>0)
					vo.setInt("percentvalue", Integer.parseInt(percentvalue));
				
				vo.setDouble("strict", Double.parseDouble(strict));
				vo.setInt("flag", Integer.parseInt(flag));
				vo.setDouble("xishu", Double.parseDouble(xishu));
				dao.addValueObject(vo);
				this.getFormHM().put("info", "addend");
		    } else
		    {
		    	int per_id=Integer.parseInt(id);
				vo.setInt("id", per_id);
				vo.setInt("degree_id", degree_id);
				vo.setString("itemname", itemname);
				vo.setDouble("topscore", Double.parseDouble(topscore));
				vo.setDouble("bottomscore", Double.parseDouble(bottomscore));
				vo.setString("itemdesc", itemdesc);
				
				if(percentvalue!=null && percentvalue.trim().length()>0)
					vo.setInt("percentvalue", Integer.parseInt(percentvalue));
				
				vo.setDouble("strict", Double.parseDouble(strict));
				vo.setInt("flag", Integer.parseInt(flag));
				vo.setDouble("xishu", Double.parseDouble(xishu));
				dao.updateValueObject(vo);
				this.getFormHM().put("info", "updateend");
		    }
			//级联上下等级项目的分值上下限
			String sql = "select * from per_degreedesc where degree_id="+degree_id+" order by id";
			this.frowset = dao.search(sql.toString());
			String preOne="";
			String nextOne="";
			int per_id=Integer.parseInt(id);
			String poi_id = String.valueOf(per_id); 
			while (this.frowset.next())
			{
				String temp = this.frowset.getString("id");
				if(!temp.equals(poi_id) && nextOne.length()==0)			
					preOne=temp;
				else if(temp.equals(poi_id) && nextOne.length()==0)
					nextOne=poi_id;
				else if(!temp.equals(poi_id) && nextOne.equals(poi_id))
					nextOne=temp;
			}
			//级联更新前一条的分值下限
			if(!"".equals(preOne) && !preOne.equals(poi_id))
			{
				vo = new RecordVo("per_degreedesc");
				int preOne_id=Integer.parseInt(preOne);
				vo.setInt("id", preOne_id);
			    vo = dao.findByPrimaryKey(vo);
				vo.setDouble("bottomscore", Double.parseDouble(topscore));
				
				PerDegreedescBo bo = new PerDegreedescBo(this.getFrameconn());
				boolean isForL = bo.isFirstOrLast(preOne, degreeId);
				if(!isForL)
				{
					//更新混合先算分值 混合先算比例中间等级项目的flag值
					if( "2".equals(degreeFlag))//混合先算分值
						vo.setInt("flag", 1);
					else if("3".equals(degreeFlag))//混合先算比例
						vo.setInt("flag", 0);
				}			
				dao.updateValueObject(vo);				
			}
			//级联更新后一条的分值上限
			if(!"".equals(nextOne) && !nextOne.equals(poi_id))
			{
				vo = new RecordVo("per_degreedesc");
				int nextOne_id=Integer.parseInt(nextOne);
				vo.setInt("id", nextOne_id);
			    vo = dao.findByPrimaryKey(vo);
				vo.setDouble("topscore", Double.parseDouble(bottomscore));
				dao.updateValueObject(vo);	
			}
			
		} catch (Exception exx)
		{
		    exx.printStackTrace();
		    throw GeneralExceptionHandler.Handle(exx);
		} 
    }

    public synchronized int getId() throws GeneralException
    {
		int num = 0; // 序号默认为0
		String sql = "select max(id) as num  from per_degreedesc";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try
		{
		    this.frowset = dao.search(sql.toString());
		    if (this.frowset.next())
		    {
		    	num = this.frowset.getInt("num");
		    }
		} catch (Exception e)
		{
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
		return num + 1;
    }

}
