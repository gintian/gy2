package com.hjsj.hrms.transaction.train.traincourse;

import com.hjsj.hrms.businessobject.train.TrainBudgetBo;
import com.hjsj.hrms.businessobject.train.TrainClassBo;
import com.hjsj.hrms.businessobject.train.TransDataBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * <p>
 * Title:培训班
 * </p>
 * <p>
 * Description:删除培训班
 * </p>
 * <p>
 * Company:HJSJ
 * </p>
 * <p>
 * Create Time:2007-12-13 下午06:07:55
 * </p>
 * 
 * @author lilinbing
 * @version 4.0
 */
public class UpdateTrainTrans extends IBusiness
{
    public void execute() throws GeneralException
    {

	HashMap hm = this.getFormHM();
	String name = (String) hm.get("data_table_table");
	cat.debug("table name=" + name);
	ArrayList list = (ArrayList) hm.get("data_table_record");
	String model = "";
	ContentDAO dao = new ContentDAO(this.getFrameconn());
	try
	{
	    String namestr = "";
	    String msg = "";
	    ArrayList valuelist = new ArrayList();

	    for (int i = 0; i < list.size(); i++)
	    {
	    	RecordVo vo = (RecordVo) list.get(i);
	    	String sp = vo.getString("r3127");
	    	namestr = vo.getString("r3130");
	    	namestr=namestr.replaceAll("%26lt;","<").replaceAll("%26gt;",">");
	    	model = vo.getString("model");
	    	
	    	String stratdate = vo.getString("r3113");
	        String enddate = vo.getString("r3114");
	        String datestr = vo.getString("r3115");
	        String dateend = vo.getString("r3116");
            String r3130 = vo.getString("r3130");
            
            String flag = TrainClassBo.checkClassDate(r3130, stratdate, enddate, datestr, dateend);
            if(!"true".equalsIgnoreCase(flag)){
                msg += "\r\n" + flag;
            }
            
	    	if ("1".equals(model))
	    	{
	    		if ("02".equals(sp))
	    		{
	    			continue;
	    		} else if ("03".equals(sp))
	    		{
	    			continue;
	    		} else if ("04".equals(sp))
	    		{
	    			continue;
	    		} else if ("05".equals(sp))
	    		{
	    			continue;
	    		} else if ("06".equals(sp))
	    		{
	    			continue;
	    		}
	    		RecordVo old_vo = new RecordVo("R31");
				old_vo.setString("r3101", vo.getString("r3101"));
				old_vo = dao.findByPrimaryKey(old_vo);
				vo.setString("r3117", old_vo.getString("r3117"));
	    		
	    	} else if ("2".equals(model))
	    	{
	    		if ("01".equals(sp))
	    		{
	    			continue;
	    		} else if ("07".equals(sp))
	    		{
	    			continue;
	    		}else if ("04".equals(sp))
	    		{
	    			continue;
	    		} else if ("05".equals(sp))
	    		{
	    			continue;
	    		} else if ("06".equals(sp))
	    		{
	    			continue;
	    		}
	    		RecordVo old_vo = new RecordVo("R31");
				old_vo.setString("r3101", vo.getString("r3101"));
				old_vo = dao.findByPrimaryKey(old_vo);
				vo.setString("r3117", old_vo.getString("r3117"));
	    	} else if ("3".equals(model))
	    	{
	    		if ("04".equals(sp))
	    		{
	    			continue;
	    		} else if ("05".equals(sp))
	    		{
	    			continue;
	    		} else if ("06".equals(sp))
	    		{
	    			continue;
	    		}
	    		String timeouto = vo.getString("timeouto");
	    		RecordVo old_vo = new RecordVo("R31");
				old_vo.setString("r3101", vo.getString("r3101"));
				old_vo = dao.findByPrimaryKey(old_vo);
				vo.setString("r3117", old_vo.getString("r3117"));
	    		if (timeouto != null && "1".equals(timeouto))
	    		{
	    			String r3115 = vo.getString("r3115");
	    			String r3116 = vo.getString("r3116");
	    			
	    			TransDataBo bo = new TransDataBo(this.getFrameconn());
	    			String theHour = bo.getStudyHour();
	    			
	    			if(theHour==null||theHour.trim().length()<1)
	    				throw GeneralExceptionHandler.Handle(new GeneralException("","标准学时不能为空，请先设置标准学时！","",""));
	    			
	    			if (r3115.equals(r3116))
	    				vo.setString("r3112", theHour);
	    			else
	    			{
	    				long days = getDays(r3115, r3116);
	    				float ndays = Float.parseFloat(String.valueOf(days));
	    				ndays *= Float.parseFloat(theHour);
	    				vo.setString("r3112", new Float(ndays).toString());
	    			}
	    		}
	    		vo.removeValue("timeouto");
	    	}
	    	vo.removeValue("model");
	    	vo.removeValue("person");
	    	String numFlag = TrainClassBo.CheckNumber(vo);
	    	if(!"true".equalsIgnoreCase(numFlag))
	    	    msg += numFlag;
	    	
	    	valuelist.add(vo);
	    }
	    
	    if(msg != null && msg.length() > 0)
	        throw new GeneralException("",msg,"","");
	    
	    if (valuelist.size() > 0)
	    {
	    	//培训预算
			TrainBudgetBo tbb = new TrainBudgetBo(this.getFrameconn());
			if(tbb.getBudget()!=null&&tbb.getBudget().length()>0){
				for (int i = 0; i < valuelist.size(); i++) {
						RecordVo vo=(RecordVo)valuelist.get(i);
						if("03".equals(vo.getString("r3127"))||"09".equals(vo.getString("r3127")))
							tbb.updateTrainBudget("2", vo.getString("r3101"), vo.getDouble("r3111"),vo.getString("r3125"));
				}
		    }
	    	dao.updateValueObject(valuelist);
	    }
	    if("3".equals(model)){
	    	//陈旭光:手动计算课时时同时更新学员信息中的课时
			StringBuffer sql = new StringBuffer();
			sql.append("update r40 set r4008=(");
			sql.append("select r31.r3112 from r31 where r40.r4005=r31.r3101");
			sql.append(") where r40.r4005 in(");
			sql.append("select r3101 from r31 where r31.r3115 is not null and r31.r3116 is not null and r31.r3127='09'");			
			sql.append(")");

			dao.update(sql.toString());
	    }
	} catch (Exception ex)
	{
	    throw GeneralExceptionHandler.Handle(ex);
	}
    }

    // 计算两个日期之间的天数
    public long getDays(String date1, String date2)
    {

	long days = 0;

	SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	try
	{
	    Date dt1 = sDateFormat.parse(date1);
	    Date dt2 = sDateFormat.parse(date2);

	    long l = dt2.getTime() - dt1.getTime();
	    days = l / 60 / 60 / 1000 / 24 + 1;
	} catch (Exception e)
	{
	    System.out.println(e);
	}
	return days;
    }

}
