package com.hjsj.hrms.transaction.kq.feast_manage;

import com.hjsj.hrms.businessobject.kq.interfaces.KqAppInterface;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 得到个人休假记录
 * <p>Title:LeaveRecordTrans.java</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Mar 21, 2007 2:34:04 PM</p>
 * @author sunxin
 * @version 1.0
 *
 */
public class LeaveRecordTrans extends IBusiness{
	
    public void execute() throws GeneralException
    {
        HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
        String a0100=(String)hm.get("a0100");
        a0100 = PubFunc.decrypt(a0100);
        String nbase=(String)hm.get("nbase");
        nbase = PubFunc.decrypt(nbase);
        String q1709=(String)hm.get("q1709");  
        q1709 = PubFunc.decrypt(q1709);
        String start_date=(String)hm.get("start_date");
        start_date = PubFunc.decrypt(start_date);
        String end_date=(String)hm.get("end_date");
        end_date = PubFunc.decrypt(end_date);
        StringBuffer sql=new StringBuffer();
        sql.append("select q15z1,q15z3,q1507,b0110,e0122,a0101,q1519 from q15 where ");
        sql.append(" a0100='"+a0100+"'");
        sql.append(" and nbase='"+nbase+"'");
        sql.append(" and q15z1>="+Sql_switcher.dateValue(start_date+" 00:00:00")+"");	
	    sql.append(" and q15z3<="+Sql_switcher.dateValue(end_date+" 23:59:59"));
	    sql.append(" and q1503 IN (" + KqAppInterface.getMapTypeIdsFromHolidayMap(q1709) +")");
	    sql.append(" and q15z5 = '03' order by q15z1");
	    ContentDAO dao=new ContentDAO(this.getFrameconn());
	    ArrayList list=new ArrayList();
	    try
	    {
	    	
	    	this.frowset=dao.search(sql.toString());	    	
	    	while(this.frowset.next())
	    	{
	    		RecordVo vo=new RecordVo("q15");
	    		vo.setString("q15z1",DateUtils.format(this.frowset.getTimestamp("q15z1"),"yyyy.MM.dd HH:mm"));
	    		vo.setString("q15z3",DateUtils.format(this.frowset.getTimestamp("q15z3"),"yyyy.MM.dd HH:mm"));
	    		vo.setString("q1507",this.frowset.getString("q1507"));
	    		vo.setString("b0110",this.frowset.getString("b0110"));
	    		vo.setString("e0122",this.frowset.getString("e0122"));
	    		vo.setString("a0101",this.frowset.getString("a0101"));
	    		String appTypeFlag = this.frowset.getString("q1519");
	    		vo.setString("q1519",appTypeFlag == null?"1":"0");
	    		list.add(vo);
	    	}
	    }catch(Exception e)
	    {
	    	e.printStackTrace();
	    }
	    this.getFormHM().put("vo_list",list);
    }

}
