package com.hjsj.hrms.transaction.performance.nworkdiary.myworkdiary;

import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class SearcHomeTrans extends IBusiness{
    String[] week= new String[]{
    		ResourceFactory.getProperty("kq.kq_rest.sunday"),
    		ResourceFactory.getProperty("kq.kq_rest.monday"),
    		ResourceFactory.getProperty("kq.kq_rest.tuesday"),
    		ResourceFactory.getProperty("kq.kq_rest.wednesday"),
    		ResourceFactory.getProperty("kq.kq_rest.thursday"),
    		ResourceFactory.getProperty("kq.kq_rest.firday"),
    		ResourceFactory.getProperty("kq.kq_rest.Saturday")
    		
            };
	public void execute() throws GeneralException {
		try{
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			String type=(String)map.get("type");//=0从首页默认进来，=1增加一周，=-1减少一周
			String hp_start="";
			String hp_end="";
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			String currDateStr=format.format(new Date());
			ArrayList gridList=new ArrayList();
			if("0".equals(type)){
				Calendar calendar = Calendar.getInstance();
				int curr_week=calendar.get(Calendar.DAY_OF_WEEK);
				Calendar scal = Calendar.getInstance();
				Calendar ecal = Calendar.getInstance();
				scal.add(Calendar.DAY_OF_MONTH, -(curr_week-1));
				hp_start=format.format(scal.getTime());
				ecal.add(Calendar.DAY_OF_MONTH, (7-curr_week));
				hp_end = format.format(ecal.getTime());
				for(int i=0;i<7;i++){
					Calendar temp = Calendar.getInstance();
					temp.set(Calendar.YEAR, scal.get(Calendar.YEAR));
					temp.set(Calendar.MONTH, scal.get(Calendar.MONTH));
					temp.set(Calendar.DAY_OF_MONTH, scal.get(Calendar.DAY_OF_MONTH));
					temp.add(Calendar.DAY_OF_MONTH, i);
					LazyDynaBean daybean=new LazyDynaBean();
					daybean.set("day", temp.get(Calendar.DAY_OF_MONTH)+"");
					daybean.set("week", week[i]);
					daybean.set("str",format.format(temp.getTime()));
					gridList.add(daybean);
				}
			}else if("1".equals(type)){//add
				hp_start=(String)this.getFormHM().get("hp_start");
                String[] arr = hp_start.split("-");
				Calendar scal = Calendar.getInstance();
				Calendar ecal = Calendar.getInstance();
				scal.set(Calendar.YEAR, Integer.parseInt(arr[0]));
				scal.set(Calendar.MONTH, Integer.parseInt(arr[1])-1);
				scal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(arr[2]));
				ecal.set(Calendar.YEAR, Integer.parseInt(arr[0]));
				ecal.set(Calendar.MONTH, Integer.parseInt(arr[1])-1);
				ecal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(arr[2]));
				scal.add(Calendar.DAY_OF_MONTH, -7);
				ecal.add(Calendar.DAY_OF_MONTH, -1);
				hp_start=format.format(scal.getTime());
				hp_end = format.format(ecal.getTime());
				for(int i=0;i<7;i++){
					Calendar temp = Calendar.getInstance();
					temp.set(Calendar.YEAR, scal.get(Calendar.YEAR));
					temp.set(Calendar.MONTH, scal.get(Calendar.MONTH));
					temp.set(Calendar.DAY_OF_MONTH, scal.get(Calendar.DAY_OF_MONTH));
					temp.add(Calendar.DAY_OF_MONTH, i);
					LazyDynaBean daybean=new LazyDynaBean();
					daybean.set("day", temp.get(Calendar.DAY_OF_MONTH)+"");
					daybean.set("week", week[i]);
					daybean.set("str",format.format(temp.getTime()));
					gridList.add(daybean);
				}
			}else if("-1".equals(type)){
				hp_end=(String)this.getFormHM().get("hp_end");
                String[] arr = hp_end.split("-");
				Calendar scal = Calendar.getInstance();
				Calendar ecal = Calendar.getInstance();
				scal.set(Calendar.YEAR, Integer.parseInt(arr[0]));
				scal.set(Calendar.MONTH, Integer.parseInt(arr[1])-1);
				scal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(arr[2]));
				ecal.set(Calendar.YEAR, Integer.parseInt(arr[0]));
				ecal.set(Calendar.MONTH, Integer.parseInt(arr[1])-1);
				ecal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(arr[2]));
				scal.add(Calendar.DAY_OF_MONTH, 1);
				ecal.add(Calendar.DAY_OF_MONTH, 7);
				hp_start=format.format(scal.getTime());
				hp_end = format.format(ecal.getTime());
				for(int i=0;i<7;i++){
					Calendar temp = Calendar.getInstance();
					temp.set(Calendar.YEAR, scal.get(Calendar.YEAR));
					temp.set(Calendar.MONTH, scal.get(Calendar.MONTH));
					temp.set(Calendar.DAY_OF_MONTH, scal.get(Calendar.DAY_OF_MONTH));
					temp.add(Calendar.DAY_OF_MONTH, i);
					LazyDynaBean daybean=new LazyDynaBean();
					daybean.set("day", temp.get(Calendar.DAY_OF_MONTH)+"");
					daybean.set("week", week[i]);
					daybean.set("str",format.format(temp.getTime()));
					gridList.add(daybean);
				}
			}
			this.getFormHM().put("hp_start", hp_start);
			this.getFormHM().put("hp_end", hp_end);
			this.getFormHM().put("currDateStr", currDateStr);
			this.getFormHM().put("gridList", gridList);
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
