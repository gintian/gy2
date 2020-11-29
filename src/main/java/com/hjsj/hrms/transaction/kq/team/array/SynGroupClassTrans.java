package com.hjsj.hrms.transaction.kq.team.array;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.kq.team.BaseClassShift;
import com.hjsj.hrms.businessobject.kq.team.KqClassArrayConstant;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.util.ArrayList;
/**
 * 同步班组人员
 * <p>Title:SynGroupClassTrans.java</p>
 * <p>Description>:SynGroupClassTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Mar 31, 2010 8:01:42 PM</p>
 * <p>@version: 5.0</p>
 * <p>@author: s.xin
 */
public class SynGroupClassTrans extends IBusiness implements KqClassArrayConstant{

	public void execute() throws GeneralException
	{
        String a_code = (String) this.getFormHM().get("a_code");
        a_code = PubFunc.decrypt(a_code);
        String start_date = (String) this.getFormHM().get("start_date");
        String end_date = (String) this.getFormHM().get("end_date");
        String session_data = (String) this.getFormHM().get("session_data");
        //取值范围 1：区间；2：时间
        String syc_type = (String) this.getFormHM().get("syc_type");
        if ("1".equalsIgnoreCase(syc_type)) {
            if (session_data == null || session_data.length() <= 0)
                throw GeneralExceptionHandler.Handle(new GeneralException("没有得到考勤期间，请与管理员联系！"));

            ArrayList datelist = RegisterDate.getKqDate(this.getFrameconn(), session_data);
            start_date = (String) datelist.get(0);
            end_date = (String) datelist.get(datelist.size() - 1);
        } else {
            start_date = PubFunc.replace(start_date, "-", ".");
            end_date = PubFunc.replace(end_date, "-", ".");
        }

        String value = a_code.substring(2, a_code.length());
        String tmp = a_code.substring(0, 2);
        if ("ep".equalsIgnoreCase(tmp)) {
            String nbase = (String) this.getFormHM().get("grnbase");
            getbmtable(value, nbase, start_date, end_date); //更新人员班组
        } else {
            String group_id = a_code.substring(2);
            BaseClassShift baseClassShift = new BaseClassShift(this.userView, this.getFrameconn());
            String t_table = baseClassShift.tempClassTable();
            String date_Table = baseClassShift.creat_KqTmp_Table(this.userView.getUserId());
            insertDate_TableData(date_Table, group_id, start_date, end_date);
            //		   baseClassShift.synchronizationInitGtoupEmployee_Table(group_id);//初始化 原来的慢，改进增加个时间限制
            baseClassShift.synchronizationInitGtoupEmployee_Tablewy(group_id, start_date, end_date);//初始化
            insrtGroupTempData(t_table, date_Table, group_id);//插入临时表
            baseClassShift.insertClassToShift(t_table);
            baseClassShift.deleteTable(t_table);
            baseClassShift.deleteTable(date_Table);
        }
	}
	/**
	 * 向时间临时表插入值
	 * @param date_Table
	 * @param group_id
	 * @param start_date
	 * @param end_date
	 */
	private void insertDate_TableData(String date_Table,String group_id,String start_date,String end_date)
	{
		StringBuffer insertSql=new StringBuffer();
		insertSql.append("INSERT INTO "+date_Table+"(orgid,sDate,dkind) ");
		insertSql.append("SELECT  org_dept_id,q03z0, class_id ");
		insertSql.append(" FROM kq_org_dept_shift");
		insertSql.append(" WHERE 1=1 ");
		insertSql.append(" and org_dept_id='"+group_id+"'");	
		insertSql.append(" and q03z0>='"+start_date+"'");
		insertSql.append(" and q03z0<='"+end_date+"'");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
			 ArrayList list=new ArrayList();
		     dao.insert(insertSql.toString(),list); 
		}catch(Exception e)
		{
			 e.printStackTrace();			
		}
	}
	
	private void insrtGroupTempData(String t_table,String date_Table,String group_id)throws GeneralException
	{
        StringBuffer insertSql = new StringBuffer();
        String srcTab = "kq_group_emp";//源表
        String insetWhere = "and " + srcTab + ".group_id='" + group_id + "'";

        insertSql.append("INSERT INTO " + t_table + "(nbase,A0100,B0110,E0122,E01A1,A0101,Q03Z0,class_id) ");
        insertSql.append("SELECT  nbase,A0100, B0110, " + Sql_switcher.isnull("E0122", "''") + ", ");
        insertSql.append(Sql_switcher.isnull("E01A1", "''") + ", A0101, DT.sDate AS q03z5,DT.dkind ");
        insertSql.append(" FROM " + srcTab + " , " + date_Table + " DT");
        insertSql.append(" WHERE 1=1 ");
        insertSql.append(insetWhere);
        insertSql.append(" AND " + RegisterInitInfoData.getKqEmpPrivWhr(this.frameconn, this.userView, srcTab));
        
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        try {
            ArrayList list = new ArrayList();
            dao.insert(insertSql.toString(), list);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
	}
	
	//更改人员班组
	private void getbmtable(String a_code,String nbase,String start_date_save,String end_date_save)
	{
		//1.先把这一个时间段的人员排班信息删除；第二步在建立新的排班信息
		StringBuffer sql = new StringBuffer();
		ArrayList list = new ArrayList();
		sql.append("delete from kq_employ_shift where ");
		sql.append(" nbase=? and a0100=? and q03z0>=? and q03z0<=?");
		ArrayList one_value= new ArrayList();
		one_value.add(nbase);
		one_value.add(a_code);
		one_value.add(start_date_save);
		one_value.add(end_date_save);
		list.add(one_value);
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		RowSet rowSet = null;
		try
		{
			dao.batchUpdate(sql.toString(), list);
			//2.找对应的班组
			String classId="";
			sql.setLength(0);
			sql.append("select group_id from kq_group_emp where nbase='"+nbase+"' and a0100='"+a_code+"'");
			rowSet=dao.search(sql.toString());
			if(rowSet.next())
			{
				classId=rowSet.getString("group_id");
			}
			//2,创建新的班次
			BaseClassShift baseClassShift=new BaseClassShift(this.userView,this.getFrameconn());
			ArrayList date_list=baseClassShift.getDatelist(start_date_save,end_date_save);  //时间list
			//得到人员的值
			sql.setLength(0);
			sql.append("select a0100,e01a1,e0122,b0110,a0101 from "+nbase+"A01 where");
			sql.append(" a0100='"+a_code+"'");
			rowSet=dao.search(sql.toString());
			list = new ArrayList();
			if(rowSet.next())
			{
				LazyDynaBean bean = new LazyDynaBean();
				String a0100=rowSet.getString("a0100");
				bean.set("a0100",a0100);
				bean.set("nbase2",nbase);
				String e01a1=rowSet.getString("e01a1"); //职位
				if(e01a1==null)
					e01a1="";
				bean.set("e01a1",e01a1);
				String e0122=rowSet.getString("e0122"); //部门
				if(e0122==null)
					e0122="";
				bean.set("e0122",e0122);
				String b0110=rowSet.getString("b0110"); //单位
				if(b0110==null)
					b0110="";
				bean.set("b0110",b0110);
				String a0101=rowSet.getString("a0101");//姓名
				if(a0101==null)
					a0101="";
				bean.set("a0101",a0101);
				list.add(bean);
			}
			for(int i=0;i<list.size();i++)
			{
				LazyDynaBean bean=(LazyDynaBean)list.get(i);
				saveInheritCalss(bean,date_list,classId); //得到每个人进行排版
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			KqUtilsClass.closeDBResource(rowSet);
		}
		
	}
	/**
	 * 给每个人建立
	 * @param bean
	 * @param date_list
	 * group_id 班组ID
	 */
	private void saveInheritCalss(LazyDynaBean bean,ArrayList date_list,String group_id)
	{
		try
		{
			String a0100=(String)bean.get("a0100");
			String nbase=(String)bean.get("nbase2");
			String e01a1=(String)bean.get("e01a1"); //职位
			String e0122=(String)bean.get("e0122"); //部门
			String b0110=(String)bean.get("b0110"); //单位
			String a0101=(String)bean.get("a0101");//姓名
			String date_Table=creat_KqTmp_Table(this.userView.getUserId()); //建立第一个临时表
			initializtion_date_Table(date_list,date_Table,nbase,e01a1,e0122,b0110,a0100,a0101); //给每个人建立一个时间范围
			getbmtable(a0100,nbase,e01a1,e0122,b0110,a0101,date_Table,group_id); //排班 把人员放入 到员工排班信息
			dropTable(date_Table);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 建立时间临时表
	 * @param userid
	 * @return
	 * @throws GeneralException
	 */
	private String creat_KqTmp_Table(String userid)throws GeneralException
	{
		String tablename=getTmpTableName(this.userView.getUserName(),RegisterInitInfoData.getKqPrivCode(userView));
		DbWizard dbWizard =new DbWizard(this.getFrameconn());
		Table table=new Table(tablename);
		if(dbWizard.isExistTable(tablename,false))
		{
			dropTable(tablename);
		}
		Field temp = new Field("nbase","人员库");
		temp.setDatatype(DataType.STRING);
		temp.setLength(50);
		temp.setKeyable(false);			
		temp.setVisible(false);
		table.addField(temp);
		Field temp1 = new Field("a0100","人员编号");
		temp1.setDatatype(DataType.STRING);
		temp1.setLength(50);
		temp1.setKeyable(false);			
		temp1.setVisible(false);
		table.addField(temp1);
		Field temp2=new Field("sDate","考勤日期");
		temp2.setDatatype(DataType.STRING);
		temp2.setLength(20);
		temp2.setKeyable(false);			
		temp2.setVisible(false);
		table.addField(temp2);
		Field temp3=new Field("b0110","单位");
		temp3.setDatatype(DataType.STRING);
		temp3.setLength(20);
		temp3.setKeyable(false);			
		temp3.setVisible(false);
		table.addField(temp3);
		Field temp4=new Field("e0122","部门");
		temp4.setDatatype(DataType.STRING);
		temp4.setLength(20);
		temp4.setKeyable(false);			
		temp4.setVisible(false);
		table.addField(temp4);
		Field temp5=new Field("e01a1","职位");
		temp5.setDatatype(DataType.STRING);
		temp5.setLength(20);
		temp5.setKeyable(false);			
		temp5.setVisible(false);
		table.addField(temp5);
		Field temp6=new Field("a0101","姓名");
		temp6.setDatatype(DataType.STRING);
		temp6.setLength(20);
		temp6.setKeyable(false);			
		temp6.setVisible(false);
		table.addField(temp6);
		try
		{
			dbWizard.createTable(table);
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}	
	
		return tablename;
	}
	
	/**
     * 新建临时表的名字
     * **/
    private String getTmpTableName(String UserId,String PrivCode) 
    {
    	StringBuffer tablename=new StringBuffer();
		tablename.append("kqgerenclass");
		tablename.append("_");
		tablename.append(PrivCode);
		tablename.append("_");
		tablename.append(UserId);
		return tablename.toString();
    }
    
    /**
	 * 删除临时表
	 * @param tablename
	 */
	private void dropTable(String tablename)
	{
		String deleteSQL="delete from "+tablename+"";		
		ArrayList deletelist= new ArrayList();	
		
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try
		{
			dao.delete(deleteSQL,deletelist);
			DbWizard dbWizard =new DbWizard(this.getFrameconn());
			Table table=new Table(tablename);
			dbWizard.dropTable(table);
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}		
	}
	/**
	 * 生成初始时间表 初始数据
	 * @param date_list
	 * @param date_Table
	 * @param nbase
	 * @param e01a1
	 * @param e0122
	 * @param b0110
	 * @param a0100
	 * @return
	 * @throws GeneralException
	 */
	private ArrayList initializtion_date_Table(ArrayList date_list,String date_Table,String nbase,String e01a1,String e0122,String b0110,String a0100,String a0101) throws GeneralException
	{
		String deleteSQL="delete from "+date_Table;		
		ArrayList deletelist= new ArrayList();
		String insertSQL="insert into "+date_Table+" (nbase,a0100,sDate,b0110,e0122,e01a1,a0101) values (?,?,?,?,?,?,?)";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		ArrayList insertList= new ArrayList();
		try
		{
			dao.delete(deleteSQL,deletelist);
			for(int i=0;i<date_list.size();i++)
			{
				String cur_date = date_list.get(i).toString();
				ArrayList  list = new ArrayList();
				list.add(nbase);
				list.add(a0100);
				list.add(cur_date);
				list.add(b0110);
				list.add(e0122);
				list.add(e01a1);
				list.add(a0101);
				insertList.add(list);
			}
			dao.batchInsert(insertSQL,insertList);
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return insertList;
	}
	/**
	 * 判断 为人员生成排班
	 * @param a0100
	 * @param nbase
	 * @param e01a1
	 * @param e0122
	 * @param b0110
	 * @param a0101
	 * @param date_Table
	 * group_id 班组ID
	 */
	private void getbmtable(String a0100,String nbase,String e01a1,String e0122,String b0110,String a0101,String date_Table,String group_id)
	{
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try
		{
			StringBuffer  insertSQL=new StringBuffer();
			insertSQL.append("INSERT INTO kq_employ_shift(nbase,A0100,A0101,B0110,E0122,E01A1,Q03Z0,class_id,status)");
			insertSQL.append("SELECT a.nbase,a.A0100,a.a0101,a.b0110,a.e0122,a.e01a1,a.sDate,b.class_id,0 ");
			insertSQL.append(" FROM "+date_Table+" a,kq_org_dept_shift b");
			insertSQL.append(" WHERE b.q03z0=a.sDate and b.org_dept_id='"+group_id+"' and b.codesetid='@G'");
			ArrayList list=new ArrayList();
			dao.insert(insertSQL.toString(),list);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
