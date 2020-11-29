package com.hjsj.hrms.transaction.performance.options;

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

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * <p>Title:ListPerDegreedescTrans.java</p>
 * <p>Description>:项目等级分类</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Jan 17, 2011 12:15:35 AM</p>
 * <p>@version: 5.0</p>
 * <p>@author: JinChunhai
 */

public class ListPerDegreedescTrans extends IBusiness
{

    public void execute() throws GeneralException
    {
		Table table = new Table("per_degreedesc");
		DbWizard dbWizard = new DbWizard(this.frameconn);
		DBMetaModel dbmodel = new DBMetaModel(this.getFrameconn());
		if (!dbWizard.isExistField("per_degreedesc", "xishu",false))
		{
		    Field obj = new Field("xishu");
		    obj.setDatatype(DataType.FLOAT);
		    obj.setLength(12);
		    obj.setDecimalDigits(6);
		    obj.setKeyable(false);
		    table.addField(obj);
		    dbWizard.addColumns(table);
		    dbmodel.reloadTableModel("per_degreedesc");
		}	
		
	//	RecordVo vo = new RecordVo("per_degreedesc");
	//	if(!vo.hasAttribute("xishu"))
	//	{
	//	    Field obj = new Field("xishu");
	//	    obj.setDatatype(DataType.FLOAT);
	//	    obj.setLength(12);
	//	    obj.setDecimalDigits(6);
	//	    obj.setKeyable(false);
	//	    table.addField(obj);
	//	    dbWizard.addColumns(table);
	//	}
		
		String degreeId = (String) this.getFormHM().get("degreeId");
		try
		{
		    String flag = this.getFlag(degreeId);
		    ArrayList setlist = this.searchPerDegreedescList(degreeId,flag);
		    this.getFormHM().put("setlist", setlist);
		    this.getFormHM().put("degreeId", degreeId);
		    this.getFormHM().put("flag", flag);
		    
		} catch (Exception ex)
		{
		    ex.printStackTrace();
		    throw GeneralExceptionHandler.Handle(ex);
		}
    }

    public String getFlag(String degreeId) throws GeneralException
    {

		String flag = "0";
		if(degreeId.trim().length()==0)
			return flag;
		StringBuffer strsql = new StringBuffer();
		strsql.append("select degree_id,flag from per_degree where degree_id=");
		strsql.append(degreeId);
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try
		{
		    this.frowset = dao.search(strsql.toString());
		    if (this.frowset.next())
			flag = this.frowset.getString("flag");
		    
		} catch (SQLException e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return flag;
    }

    public ArrayList searchPerDegreedescList(String degreeId,String flag) throws GeneralException
    {
    	DecimalFormat myformat1 = new DecimalFormat("##########.#####");
		if("0".equals(flag) || "1".equals(flag))
		    return searchPerDegreedescList2(degreeId,flag);
		
		ArrayList list = new ArrayList();
		
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try
		{    
		    int count=0;
		    this.frowset = dao.search("select count(*) from per_degreedesc where  degree_id="+ degreeId);
		    if(this.frowset.next())
			count= this.frowset.getInt(1);
		    //flag=2 混合先算分值 flag=3 混合先算比例
		    StringBuffer buf = new StringBuffer();
		    buf.append("select * from per_degreedesc where  degree_id=" + degreeId);	 
		    buf.append(" order by id");
		    this.frowset = dao.search(buf.toString());
		    int i=1;
		    while (this.frowset.next())
		    {
				RecordVo vo = new RecordVo("per_degreedesc");
				vo.setString("id", this.frowset.getString("id"));
				vo.setString("degree_id", this.frowset.getString("degree_id"));
				vo.setString("itemname", this.frowset.getString("itemname"));
								
				String topscore=(String)this.frowset.getString("topscore")==null?"":(String)this.frowset.getString("topscore");
				if(topscore!=null && topscore.trim().length()>0)
					topscore=Double.toString(Double.parseDouble(topscore));//去掉小数点后面的0																			
				vo.setString("topscore", topscore);
				
				String bottomscore = (String)this.frowset.getString("bottomscore")==null?"":(String)this.frowset.getString("bottomscore");
				if(bottomscore!=null && bottomscore.trim().length()>0)
					bottomscore=Double.toString(Double.parseDouble(bottomscore));//去掉小数点后面的0
				vo.setString("bottomscore", bottomscore);
				
				vo.setString("itemdesc", PubFunc.toHtml(this.frowset.getString("itemdesc")));		
				String precentvalue = this.frowset.getString("percentvalue")==null?"":this.frowset.getString("percentvalue");
				if("2".equals(flag) && (i==1 || i==count))//混合先算分值第一条和最后一条的比例为空串
				    precentvalue="";		
				if("3".equals(flag) && i>1 && i<count)//混合先算比例第一条和最后一条的比例不为空
				    precentvalue="";
				vo.setString("percentvalue", "".equals(precentvalue)?"":precentvalue+"%");
				String strict = this.frowset.getString("strict")==null?"0":this.frowset.getString("strict");
				
				if(Double.parseDouble(strict)*100>0 && "2".equals(flag))//混合先算分值
				    strict = Double.toString(Double.parseDouble(strict)*100)+"%"; 
				else if(Double.parseDouble(strict)*100>0 && "3".equals(flag))//混合先算比例
				{
					
				}
				else
				    strict="";
				
				vo.setString("strict", strict);
				vo.setString("flag", this.frowset.getString("flag"));
				vo.setString("xishu", PubFunc.round(this.frowset.getString("xishu")==null?"":this.frowset.getString("xishu"),2));
				list.add(vo);
				i++;
		    }
	
		} catch (Exception e)
		{
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
		return list;
    }
    
    public ArrayList searchPerDegreedescList2(String degreeId,String flag) throws GeneralException
    {
    	DecimalFormat myformat1 = new DecimalFormat("##########.#####");
		ArrayList list = new ArrayList();
		if(degreeId.trim().length()==0)
			return list;
		StringBuffer buf = new StringBuffer();
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try
		{    
		    buf.append("select * from per_degreedesc where  degree_id=" + degreeId);
		    buf.append(" order by id");
		    this.frowset = dao.search(buf.toString());
		    while (this.frowset.next())
		    {
				RecordVo vo = new RecordVo("per_degreedesc");
				vo.setString("id", this.frowset.getString("id"));
				vo.setString("degree_id", this.frowset.getString("degree_id"));
				vo.setString("itemname", this.frowset.getString("itemname"));
				
				String topscore=(String)this.frowset.getString("topscore")==null?"":(String)this.frowset.getString("topscore");
				if(topscore!=null && topscore.trim().length()>0)
					topscore=Double.toString(Double.parseDouble(topscore));//去掉小数点后面的0																			
				vo.setString("topscore", topscore);
				
				String bottomscore = (String)this.frowset.getString("bottomscore")==null?"":(String)this.frowset.getString("bottomscore");
				if(bottomscore!=null && bottomscore.trim().length()>0)
					bottomscore=Double.toString(Double.parseDouble(bottomscore));//去掉小数点后面的0
				vo.setString("bottomscore", bottomscore);
				
				String precentvalue = this.frowset.getString("percentvalue")==null?"":this.frowset.getString("percentvalue");
				vo.setString("itemdesc", PubFunc.toHtml(this.frowset.getString("itemdesc")));
				vo.setString("percentvalue", "".equals(precentvalue)?"":precentvalue+"%");
				String strict = this.frowset.getString("strict")==null?"0":this.frowset.getString("strict");
				if(Double.parseDouble(strict)*100>0 && "1".equals(flag))//比例
				{
					
				}
				else if(Double.parseDouble(strict)*100>0 && "0".equals(flag))//分值
				    strict = PubFunc.round(Double.toString(Double.parseDouble(strict)*100),2)+"%";
				else
				    strict="";
				vo.setString("strict", strict);
				vo.setString("flag", this.frowset.getString("flag"));
				vo.setString("xishu", PubFunc.round(this.frowset.getString("xishu")==null?"":this.frowset.getString("xishu"),2));
				list.add(vo);
		    }
	
		} catch (Exception e)
		{
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
		return list;
    }    
}
