package com.hjsj.hrms.transaction.train.resource.trainroom;

import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.businessobject.train.resource.TrainResourceBo;
import com.hjsj.hrms.businessobject.train.resource.TrainRoomBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class SelfTrainRoomTrans extends IBusiness {

	public void execute() throws GeneralException {
		String type = (String)this.getFormHM().get("type");
		
		if ("self".equals(type)&& (this.userView.getA0100() == null || this.userView.getA0100().length() <= 0)) {
			throw GeneralExceptionHandler.Handle(new GeneralException("","非自助用户不能使用此功能！","",""));
		}
		
		Date date = new Date();
		ArrayList itemList = getYearList();
		
		String year = (String)this.getFormHM().get("year");
		year = year==null||year.length()!=4?String.valueOf(DateUtils.getYear(date)):year;
		String month = (String)this.getFormHM().get("month");
		month = month==null||month.length()<1?String.valueOf(DateUtils.getMonth(date)):month;
		month = month.length()!=2?"0"+month:month;
		
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR,Integer.parseInt(year));
		cal.set(Calendar.MONTH, Integer.parseInt(month) - 1);//Java月份从0开始算
		int dateOfMonth = cal.getActualMaximum(Calendar.DATE);
		
		String columns="r1001,r1011";
		String strsql="select r1001,r1011";
		String order_by = " order by r1001";
		StringBuffer strwhere = new StringBuffer(" from r10");
		
		//培训场所权限
		if(!this.userView.isSuper_admin())
	    {
	        TrainCourseBo tb = new TrainCourseBo(this.userView);
	        String a_code = tb.getUnitIdByBusi();
            if(a_code.indexOf("UN`")==-1){
                String unitarr[] = a_code.split("`"); 
                String str="";
                for(int i=0;i<unitarr.length;i++){
                    if(unitarr[i]!=null&&unitarr[i].trim().length()>2&&unitarr[i].startsWith("UN")){
                            str +="B0110 like '"+unitarr[i].substring(2)+"%' or ";
                    }
                }
                if(str.length()>0)
                    strwhere.append(" where (B0110='HJSJ' or "+str.substring(0, str.lastIndexOf("or")-1)+")");
                else{
                    strwhere.append(" where B0110 = 'HJSJ'");
                }
            }
	    }
		TrainResourceBo bo = new TrainResourceBo(this.frameconn, type);
		String search = (String) this.userView.getHm().get("train_strParam");
	    String wherestr = ""; 
	    if (search != null && search.trim().length() > 0)
	        wherestr = bo.getWhereStr(search);
	    
	    if(wherestr !=null && wherestr.length() > 0){
	        if(strwhere.indexOf("where")==-1)
	            strwhere.append(" where 1=1 ");
	        
	        strwhere.append(wherestr);
	    }
	    
		this.formHM.put("year", year);
		this.formHM.put("month", month);
		this.formHM.put("dateOfMonth", String.valueOf(dateOfMonth));
		this.formHM.put("itemList", itemList);
		this.formHM.put("columns", columns);
		this.formHM.put("strsql", strsql);
		this.formHM.put("strwhere", strwhere.toString());
		this.formHM.put("order_by", order_by);
		if("self".equals(type))
			new TrainRoomBo(this.getFrameconn(),year,month,userView.getDbname(),userView.getA0100());//初始化数据
		else
			new TrainRoomBo(this.getFrameconn(),year,month,null,null);//初始化数据
	}
	
	private ArrayList getYearList()
	{
	    ArrayList years = new ArrayList();
	    
	    StringBuffer sql = new StringBuffer();
	    sql.append("SELECT DISTINCT " + Sql_switcher.year("R6101") + " AS ayear");
	    sql.append(" FROM R61");
	    sql.append(" ORDER BY ayear");
	    
	    ContentDAO dao = new ContentDAO(this.frameconn);
	    try
        {
	        CommonData cd = null;
	        int ayear = 0;
	        
	        this.frowset = dao.search(sql.toString());
	        while (this.frowset.next())
            {
	            ayear = this.frowset.getInt("ayear");	            
	            
	            cd = new CommonData();
	            cd.setDataName(String.valueOf(ayear));
	            cd.setDataValue(String.valueOf(ayear));
                years.add(cd);                
            }
	        
	        Calendar ca = Calendar.getInstance();
	        int curyear = ca.get(Calendar.YEAR);
	        
	        //没有申请记录或申请都在当前年度之前，加入当前年度
	        if (ayear < curyear)
	        {
	            cd = new CommonData();
	            cd.setDataName(String.valueOf(curyear));
	            cd.setDataValue(String.valueOf(curyear));
	            years.add(cd);	            
	        }
	        
	        //如果离年底还有三个月，则加上下年度
	        if (ayear <= curyear)
	        {
	            if(9 < (ca.get(Calendar.MONTH) + 1))
	            {
	                cd = new CommonData();
	                cd.setDataName(String.valueOf(curyear + 1));
	                cd.setDataValue(String.valueOf(curyear + 1));
	                years.add(cd);
	            }                    
            }
	        
	        Collections.reverse(years);
	        
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {

        }
	    
	    return years;
	}
}