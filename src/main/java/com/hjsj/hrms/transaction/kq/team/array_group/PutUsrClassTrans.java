package com.hjsj.hrms.transaction.kq.team.array_group;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.kq.team.KqClassArrayConstant;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
/**
 * 
 * <p>Title:排班更改人员班组</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Mar 30, 2010:11:06:38 AM</p>
 * @author wangyao
 * @version 1.0
 *
 */
public class PutUsrClassTrans extends IBusiness implements KqClassArrayConstant{

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String group_id=(String)this.getFormHM().get("group_id");
		//考勤自己的取权限的方法
		String privCode=RegisterInitInfoData.getKqPrivCode(userView);
    	String privCodeValue=RegisterInitInfoData.getKqPrivCodeValue(userView);
    	
//		ArrayList dlist=(ArrayList)this.getFormHM().get("dlist");  //在职人员库
////		if(dlist==null||dlist.size()<=0)
////		this.getFormHM().put("dlist",getDbase());
//		String a0101_s=(String)this.getFormHM().get("a0101_s");
//		String dbper=(String)this.getFormHM().get("dbper");
//		/*ArrayList fieldlist = DataDictionary.getFieldList(this.kq_group_emp_table,
//				Constant.USED_FIELD_SET);*/
//		GroupsArray groupsArray=new GroupsArray();
//		ArrayList fieldlist=groupsArray.groupEmpFieldlist();
//		String columns=groupsArray.groupEmpColumns();		
//		String sqlstr="select "+columns;
//		StringBuffer  whereSTR=new StringBuffer();
//		whereSTR.append("from "+kq_group_emp_table+" where");
//		whereSTR.append(" "+kq_shift_group_Id+"='"+group_id+"'");
//		//非su用户，得到所属权限的分组人员
//    	if(!this.userView.isSuper_admin())
//    	{
//    		if(!privCodeValue.equals(""))
//    		{
//    			whereSTR.append(" and e0122 like '"+privCodeValue+"%'");
//    		}
//    	}
//    	
//		if(a0101_s!=null&&a0101_s.length()>0)
//			a0101_s=PubFunc.getStr(a0101_s);
//		if(dbper!=null&&dbper.length()>0)
//			whereSTR.append(" and nbase='"+dbper+"'");
//		if(a0101_s!=null&&a0101_s.length()>0)
//			whereSTR.append(" and a0101 like '%"+a0101_s+"%'");
		String start_date=PubFunc.getStringDate("yyyy.MM.dd");  //开始时间
//		String end_date=PubFunc.getStringDate("yyyy.MM.dd");	//结束时间
		
    	// 将起始时间改为当前期间的第一天
    	ArrayList list=RegisterDate.getKqDayList(this.getFrameconn());
		if(list!=null||list.size()>0) {			
			start_date = list.get(0).toString();
		}
		
		// 将结束时间改为考勤期间最后一天
		String end_date = KqUtilsClass.getDurationLastDay();
		
		ArrayList classlist = getclasslist(group_id); //班组
//	    this.getFormHM().put("sqlstr",sqlstr);
//	    this.getFormHM().put("where",whereSTR.toString());
//	    this.getFormHM().put("column",columns);
//	    this.getFormHM().put("fieldlist",fieldlist);
	    this.getFormHM().put("start_date",start_date);
	    this.getFormHM().put("end_date",end_date);
	    this.getFormHM().put("classlist", classlist);
	}
	public ArrayList getclasslist(String group_id)
	{
		ArrayList list = new ArrayList();
		CommonData da = new CommonData();
		StringBuffer buf = new StringBuffer();
		buf.append("select group_id,name from kq_shift_group ");
		buf.append(" where group_id!='"+group_id+"'");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		RowSet st=null;
		try
		{
			st = dao.search(buf.toString());
			da.setDataName(" ");
			da.setDataValue("#$");
			list.add(da);
			while(st.next())
			{
				if (userView.isHaveResource(IResourceConstant.KQ_CLASS_GROUP, st.getString("group_id")))
				 {
					da = new CommonData();
					da.setDataName(st.getString("name"));
					da.setDataValue(st.getString("group_id"));
					list.add(da);
				 }
				
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			if(st!=null)
			{
				try {
					st.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return list;
	}
}
