package com.hjsj.hrms.transaction.train.trainexam.exam;

import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.businessobject.train.trainexam.exam.TrainExamPlanBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * <p>
 * Title:SearchExamPlanTrans
 * </p>
 * <p>
 * Description:查询考试计划
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2011-11-10
 * </p> 
 * @author zxj
 *
 */
public class SearchExamPlanTrans extends IBusiness {

	public void execute() throws GeneralException
	{ 	
		//sql字段
		StringBuffer columns = new StringBuffer();
		ArrayList fields = DataDictionary.getFieldList("R54", Constant.USED_FIELD_SET);
		for(int i=0;i<fields.size();i++)
		{
			FieldItem item = (FieldItem)fields.get(i);
			columns.append(item.getItemid());
			if(i<(fields.size()-1))
				columns.append(",");
		}
		if (fields.indexOf("norder") < 0)
			columns.append(",norder");
		
		StringBuffer sql = new StringBuffer();
		sql.append("select "+columns.toString());		
		//orcle库 ，分页标签获取日期类型的数据时，缺失时分秒的部分。    chenxg  2014-11-05
		if(Sql_switcher.searchDbServer()==2){
		    String column = columns.toString();
		    int w = DataDictionary.getFieldItem("r5405", "r54").getItemlength();
		    String format = getDateFormat(w);
		    column = column.replace("r5405", "to_char(r5405,'" + format + "') as r5405");
		    
		    w = DataDictionary.getFieldItem("r5406", "r54").getItemlength();
            format = getDateFormat(w);
            column = column.replace("r5406", "to_char(r5406,'" + format + "') as r5406");
            sql.delete(0, sql.length());
            sql.append("select " + column);
		    
		}

		StringBuffer where = new StringBuffer(" FROM R54");
		where.append(" WHERE 1=1");
		
		String status = (String)this.getFormHM().get("status");
        if ((status!=null)&&(status.length()>0))
        {
        	if (!("all".equalsIgnoreCase(status)))
                where.append(" AND R5411='" + status + "'");
        }
          
		String showStyle = (String)this.getFormHM().get("showStyle");
        if ((showStyle!=null)&&(showStyle.length()>0))
        {
        	if (!("0".equalsIgnoreCase(showStyle)))
                where.append(" AND R5409=" + showStyle);
        }
        
        String planName = (String)this.getFormHM().get("planName");
        if ((planName!=null)&&(planName.length()>0))
        {
        	where.append(" AND R5401 LIKE '%");
        	where.append(planName);
        	where.append("%' ");
        }
        else
        {
        	planName = "";
        }
        
		String maxOrder = getMaxOrder();
		
		// 权限过滤
		StringBuffer priv = new StringBuffer();
		if (!this.userView.isSuper_admin()) 
		{			
			//权限范围内计划
			TrainCourseBo bo = new TrainCourseBo(this.userView);
			String unit = bo.getUnitIdByBusi();
			
			//不是全权
			if(!"UN`".equals(unit))
			{
				//不是0权限，加权限控制
				if(!"".equals(unit))
				{				
					String []units = unit.split("`");

					priv.append(" and (");
					for (int i = 0; i < units.length; i++) 
					{
						if (i != 0) 
						{
							priv.append(" or ");
						} 
						String b0110s = units[i].substring(2);
						priv.append("b0110 like '");
						priv.append(b0110s);
						priv.append("%'");
					}
					priv.append(" or b0110='' or "+Sql_switcher.isnull("b0110", "'-1'")+"='-1'");
					priv.append(")");
				}
				else
				{
					priv.append(" or b0110='' or "+Sql_switcher.isnull("b0110", "'-1'")+"='-1'");
				}
			}
		}
		where.append(priv);
		
		TrainExamPlanBo examPlanBo = new TrainExamPlanBo(this.frameconn);
		ArrayList statusList = examPlanBo.getStatusList();
		ArrayList showStyleList = examPlanBo.getShowSytleList();
		
		this.getFormHM().put("sqlstr",sql.toString());		
		this.getFormHM().put("column",columns.toString());
		this.getFormHM().put("where",where.toString());
		this.getFormHM().put("maxOrder", maxOrder); 
		this.getFormHM().put("statusList", statusList);
		this.getFormHM().put("showStyleList", showStyleList);
		this.getFormHM().put("planName", planName);

	}
	
	private String getMaxOrder(){
		
		//记录最大顺序号nOrder
		String order = "0";
		try
		{			
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset = dao.search("SELECT MAX(nOrder) AS maxOrder FROM R54");
			if (this.frowset.next())
			{
				order = this.frowset.getString(1);
				if (order==null)
					order = "0";
			}		    
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			
		}
		
		return order;
	}
	/**
	 * 获取orcle的日期类型指标的格式
	 * @param w
	 * @return
	 */
    private String getDateFormat(int w) {

        String format = "yyyy-MM-dd";
        if (w == 18)
            format = "yyyy-MM-dd HH24:mi:ss";
        if (w == 15)
            format = "yyyy-MM-dd HH24:mi";
        if (w == 7)
            format = "yyyy-MM";
        if (w == 4)
            format = "yyyy";

        return format;
    }

}
