package com.hjsj.hrms.transaction.performance.options;

import com.hjsj.hrms.businessobject.performance.options.PerDegreedescBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.HashMap;

/**
 * <p>Title:ListPerDegreedescTrans.java</p>
 * <p>Description>:添加或编辑等级分类</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Jan 17, 2011 12:15:35 AM</p>
 * <p>@version: 5.0</p>
 * <p>@author: JinChunhai
 */

public class InitAddPerDegreedescTrans extends IBusiness
{

    public void execute() throws GeneralException
    {

		String info = (String) this.getFormHM().get("info");
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String id = (String) hm.get("id");
		hm.remove("id");
		String degreeId = (String) this.getFormHM().get("degreeId");
	
		PerDegreedescBo bo = new PerDegreedescBo(this.getFrameconn());
		boolean isFirst = bo.isTheFirst(id, degreeId);
		if (isFirst)
		    this.getFormHM().put("itemNo", "1");
		else
		    this.getFormHM().put("itemNo", "0");
		
		boolean isForL = bo.isFirstOrLast(id, degreeId);
		if(isForL)
		    this.getFormHM().put("isForL", "1");
		else
		    this.getFormHM().put("isForL", "0");
		
		RecordVo vo = new RecordVo("per_degree");
		vo.setString("degree_id", degreeId);
		ContentDAO dao = new ContentDAO(this.getFrameconn());
	
		if(degreeId.length()==0)
			throw new GeneralException("没有等级分类，请先增加等级分类！");
		
		try
		{
		    vo = dao.findByPrimaryKey(vo);
		    String flag = vo.getString("flag");
		    this.getFormHM().put("flag", flag);
	
		    if ("edit".equals(info))
		    {
				RecordVo recordVo = bo.getVo(id);
				String topscore = recordVo.getString("topscore");
				String bottomscore = recordVo.getString("bottomscore");
				String xishu = recordVo.getString("xishu");
				//控制显示的小数位数
				recordVo.setString("xishu", xishu);								
				
				if(topscore!=null && topscore.trim().length()>0)
					topscore=Double.toString(Double.parseDouble(topscore));//去掉小数点后面的0																			
				recordVo.setString("topscore", topscore);	
				
				if(bottomscore!=null && bottomscore.trim().length()>0)
					bottomscore=Double.toString(Double.parseDouble(bottomscore));//去掉小数点后面的0
				recordVo.setString("bottomscore", bottomscore);
								
				String strict = recordVo.getString("strict");
				if(strict==null || strict!=null && strict.length()==0)
					strict="0";
				if(Double.parseDouble(strict)*100>0 && "1".equals(flag))//比例
				{
					
				}
				else if(Double.parseDouble(strict)*100>0 && "0".equals(flag))//分值
				{
					double x = Double.parseDouble(strict)*100;
					 strict = PubFunc.round(Double.toString(x),2);//+"%";
				}		   
				else if(Double.parseDouble(strict)*100>0 && "2".equals(flag))//混合先算分值
				    strict = Double.toString(Double.parseDouble(strict)*100);//+"%"; 
				else if(Double.parseDouble(strict)*100>0 && "3".equals(flag))//混合先算比例
				{
					
				}
				else
				    strict="";
				recordVo.setString("strict", strict);	
				
				this.getFormHM().put("perdegreedescvo", recordVo);
		
		    } else
		    {
				RecordVo perdegreedescvo = new RecordVo("per_degreedesc");
				perdegreedescvo.setString("id", "");
				perdegreedescvo.setString("degree_id", degreeId);
				perdegreedescvo.setString("itemname", "");
				perdegreedescvo.setString("topscore", this.getTopscore(degreeId));
				perdegreedescvo.setString("bottomscore", "");
				perdegreedescvo.setString("itemdesc", "");
				perdegreedescvo.setString("percentvalue", "");
				perdegreedescvo.setString("strict", "");
				perdegreedescvo.setString("flag", "");
				perdegreedescvo.setString("xishu", "");
				this.getFormHM().put("perdegreedescvo", perdegreedescvo);
		    }
		} catch (Exception ex)
		{
		    ex.printStackTrace();
		    throw GeneralExceptionHandler.Handle(ex);
		}
    }
    
    public String getTopscore(String degreeId) throws GeneralException 
    {
    	String score = "";
    	StringBuffer sql = new StringBuffer();
    	sql.append("select bottomscore from per_degreedesc where degree_id = "+degreeId);
    	sql.append(" and id = ");
    	sql.append("(select max(id) from per_degreedesc where degree_id = "+degreeId);
    	sql.append(") ");
    	ContentDAO dao = new ContentDAO(this.getFrameconn());
    	try
		{
			this.frowset = dao.search(sql.toString());
			if(this.frowset.next())
			{
				score = this.frowset.getString(1)!=null?this.frowset.getString(1):"";
				if(score!=null && score.trim().length()>0)
					score=Double.toString(Double.parseDouble(score));//去掉小数点后面的0	
			}
		} catch (SQLException e)
		{
			e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
    	return score;
    }
}
