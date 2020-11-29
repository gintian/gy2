/**
 * 
 */
package com.hjsj.hrms.businessobject.infor;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.sys.InfoGroup;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
/**
 * <p>Title:基础信息管理类</p>
 * <p>Description:对基本信息群的操作，人员信息移库等</p> 
 * <p>Company:hjsj</p> 
 * create time at:Nov 4, 20061:32:58 PM
 * @author chenmengqing
 * @version 4.0
 */
public class BaseInfoBo {

	private Connection conn=null;
	/**信息群种类*/
	private InfoGroup infogroup;
	/**操作用户对象*/
	private UserView userview;
	private ContentDAO dao;
	public BaseInfoBo(Connection conn,UserView userview, int inforid) {
		this.conn=conn;
		this.userview=userview;
		dao=new ContentDAO(this.conn);
		infogroup=new InfoGroup(inforid);
	}
	/**
	 * 移库时,修改相关的表的数据
	 * @param srcPre
	 * @param desPre
	 * @param srcA0100
	 * @param desA0100
	 * @throws GeneralException
	 */
	private void updateA0100(String srcPre,String desPre,String srcA0100,String desA0100)throws GeneralException
	{
		StringBuffer strsql=new StringBuffer();
		ArrayList paralist=new ArrayList();
		try
		{
			/**更新历史记录*/
			strsql.append("update salaryhistory set nbase=?,a0100=?");
			strsql.append(" where nbase=? and a0100=?");
			paralist.add(desPre);
			paralist.add(desA0100);
			paralist.add(srcPre);
			paralist.add(srcA0100);
			dao.update(strsql.toString(),paralist);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	/**
	 * 计算对应的库中最大的排序号
	 * @param strpre
	 * @return
	 */
	public int getMaxA0000(String strpre)
	{
		int na0000=1;
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			StringBuffer strsql=new StringBuffer();
			strsql.append("select max(a0000) as maxa0000 from ");
			strsql.append(strpre);
			strsql.append("A01");
			RowSet rset=dao.search(strsql.toString());
			if(rset.next()) {
                na0000=rset.getInt("maxa0000")+1;
            }
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return na0000;
	}
	
	/**
	 * 记录日志,为了数据同步
	 * @param operationtype
	 * @param srcPre
	 * @param desPre
	 * @param a0100
	 * @throws GeneralException
	 */
	private void writeMainSetLog(int operationtype,String srcPre,String desPre,String a0100)throws GeneralException
	{
		StringBuffer strsql=new StringBuffer();
		String cap=this.infogroup.getPrefix();
		String setid=infogroup.getMainset();
		FieldSet fieldset=new FieldSet(setid);
		String fields=fieldset.getFieldString();
		ArrayList paralist=new ArrayList();
		switch(operationtype)
		{
		case 1://人员库删除记录时,DBASE为空
			strsql.append("insert into ");
			strsql.append(cap);
			strsql.append("01Log(");
			strsql.append(fields);
			strsql.append(",sbase,setid,modtime1)");
			strsql.append(" select ");
			strsql.append(fields);
			strsql.append(",?,?,");
			strsql.append(Sql_switcher.sqlNow());
			strsql.append(" from ");
			strsql.append(srcPre);
			strsql.append(setid);
			strsql.append(" where ");
			strsql.append(infogroup.getKeyfield());
			strsql.append("=?");
			paralist.add(srcPre);
			paralist.add(setid);
			paralist.add(a0100);
			break;
		case 2://人员库移动记录时(删除原记录时),DBASE为目标库
			strsql.append("insert into ");
			strsql.append(cap);
			strsql.append("01Log(");
			strsql.append(fields);
			strsql.append(",sbase,dbase,setid,modtime1)");
			strsql.append(" select ");
			strsql.append(fields);
			strsql.append(",?,?,?");
			strsql.append(Sql_switcher.sqlNow());
			strsql.append(" from ");
			strsql.append(srcPre);
			strsql.append(setid);
			strsql.append(" where ");
			strsql.append(infogroup.getKeyfield());
			strsql.append("=?");
			paralist.add(srcPre);
			paralist.add(desPre);
			paralist.add(setid);
			paralist.add(a0100);			
			break;
		}
		try
		{
			dao.update(strsql.toString(),paralist);
		}	
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	/**
	 * 求最大人员编号,为了兼容以前的版本
	 * @param strpre
	 * @return
	 */
	public String getMaxA0100(String strpre)
	{
		int na0100=1;
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			StringBuffer strsql=new StringBuffer();
			strsql.append("select max(a0100) as maxa0100 from ");
			strsql.append(strpre);
			strsql.append("A01");
			RowSet rset=dao.search(strsql.toString());
			if(rset.next()) {
                na0100=rset.getInt("maxa0100")+1;
            }
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return StringUtils.leftPad(String.valueOf(na0100),8,'0');
	}
	/**
	 * 取出某个人员库中的A0100和A0000最大值
	 * @param strpre 库前缀
	 * @return String[0]:人员编号 String[1]排序号
	 */
	public String[] getMaxA0100A0000(String strpre)
	{
		String[] a0100a0000=new String[2];
		int na0100=1;
		int na0000=1;
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			StringBuffer strsql=new StringBuffer();
			strsql.append("select max(a0100) as maxa0100,max(a0000) as maxa0000 from ");
			strsql.append(strpre);
			strsql.append("A01");
			RowSet rset=dao.search(strsql.toString());
			if(rset.next())
			{
				String stra0100=rset.getString("maxa0100");
				na0100=Integer.parseInt(stra0100)+1;
				na0000=rset.getInt("maxa0000")+5;
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		a0100a0000[0]=StringUtils.leftPad(String.valueOf(na0100),8,'0');
		a0100a0000[1]=String.valueOf(na0000);
		return a0100a0000;
	}
	/**
	 * 求子集记录的最大主键
	 * @param strpre
	 * @param srctab
	 * @param key
	 * @return
	 */
	public int getMaxI9999(String strpre,String srctab,String key)
	{
		int maxi9999=1;
		RowSet rset=null;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			ArrayList paralist=new ArrayList();
			StringBuffer strsql=new StringBuffer();
			strsql.append("select max(i9999) as maxi9999 from ");
			strsql.append(strpre);
			strsql.append(srctab);
			strsql.append(" where ");
			strsql.append(this.infogroup.getKeyfield());
			strsql.append("=?");
			paralist.add(key);
			rset=dao.search(strsql.toString(),paralist);
			if(rset.next()) {
                maxi9999=rset.getInt("maxi9999")+1;
            }
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally{
			PubFunc.closeDbObj(rset);
		}
		return maxi9999;
	}
	/**
	 * 清空子集的记录
	 * @param dao
	 * @param desPre
	 * @param desTab
	 * @param keyvalue
	 * @throws GeneralException
	 */
	public void deleteAllRecord(String desPre,String desTab,String keyvalue)throws GeneralException
	{
		StringBuffer strsql=new StringBuffer();
		ArrayList paralist=new ArrayList();
		try
		{
			strsql.append("delete from ");
			strsql.append(desPre);
			strsql.append(desTab);
			strsql.append(this.infogroup.getKeyfield());
			strsql.append("=?");
			paralist.add(keyvalue);
			dao.update(strsql.toString(),paralist);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	
	private void updateCreateTime(ContentDAO dao,String strPre,String a0100)throws GeneralException
	{
		StringBuffer strsql=new StringBuffer();
		ArrayList paralist=new ArrayList();
		try
		{
			strsql.append("update ");
			strsql.append(strPre);
			strsql.append("A01 set createtime=");
			strsql.append(Sql_switcher.sqlNow());
			strsql.append(" where a0100=?");
			paralist.add(a0100);
			dao.update(strsql.toString(),paralist);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	
	private void updateModTime(ContentDAO dao,String strPre,String a0100)throws GeneralException
	{
		StringBuffer strsql=new StringBuffer();
		ArrayList paralist=new ArrayList();
		try
		{
			strsql.append("update ");
			strsql.append(strPre);
			strsql.append("A01 set modtime=");
			strsql.append(Sql_switcher.sqlNow());
			strsql.append(" where a0100=?");
			paralist.add(a0100);
			dao.update(strsql.toString(),paralist);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}	
	
	/**
	 * 删除人员
	 * @param srcPre
	 * @param desPre
	 * @param keyvalue
	 * @param operationtype  =2移动 =1删除 ,控制记录日志,
	 * @throws GeneralException
	 */
	public void DeleteOneEmployee(String srcPre,String desPre,String keyvalue,int operationtype)throws GeneralException
	{
		ArrayList setlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.EMPLOY_FIELD_SET);
		try
		{
			StringBuffer strsql=new StringBuffer();	
			String setid=null;	
			for(int i=0;i<setlist.size();i++)
			{
				strsql.setLength(0);
				FieldSet fieldset=(FieldSet)setlist.get(i);
				setid=fieldset.getFieldsetid();
				if(fieldset.isMainset())
				{
					writeMainSetLog(operationtype,srcPre,desPre,keyvalue);
				}
				deleteAllRecord(srcPre,setid,keyvalue);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	
	/**
	 * 复制一个的信息,可以在同一个库之间,也可以在不同的应用库之间
	 * @param srcPre
	 * @param desPre
	 * @param a0100
	 * @return
	 */
	public String CopyOneEmployee(String srcPre,String desPre,String a0100)throws GeneralException
	{
		/**取出已构库的所有子集*/
		ArrayList setlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.EMPLOY_FIELD_SET);
		String strDesA0100=getMaxA0100(desPre);
		String strDesA0000=String.valueOf(getMaxA0000(desPre));
		String setid=null;
		StringBuffer strsql=new StringBuffer();
		ContentDAO dao=new ContentDAO(this.conn);
		ArrayList paralist=new ArrayList();
		try
		{
			for(int i=0;i<setlist.size();i++)
			{
				strsql.setLength(0);
				paralist.clear();
				FieldSet fieldset=(FieldSet)setlist.get(i);
				setid=fieldset.getFieldsetid();
				/**清空记录,解决子集一些无头案*/
				deleteAllRecord(desPre,setid,strDesA0100);
				/**多媒体子集*/
				if("A00".equalsIgnoreCase(setid))
				{
					strsql.append("insert into ");
					strsql.append(desPre);
					strsql.append("A00");
					strsql.append("(a0100,i9999,title,flag,ole,ext) select ");
					strsql.append("?,i9999,title,flag,ole,ext from ");
					strsql.append(srcPre);
					strsql.append("A00");
					strsql.append(" where a0100=?");
					paralist.add(strDesA0100);
					paralist.add(a0100);
					dao.update(strsql.toString(),paralist);					
					continue;
				}
				/**取出已构库所有指标*/
				String strfield=fieldset.getFieldString();

				if(fieldset.isMainset())
				{
					strsql.append("Insert into ");
					strsql.append(desPre);
					strsql.append(setid);
					strsql.append("(a0100,a0000,");
					strsql.append(strfield);
					strsql.append(") select ?,?,");
					strsql.append(strfield);
					strsql.append(" from ");
					strsql.append(srcPre);
					strsql.append(setid);
					strsql.append(" where a0100=?");
					paralist.add(strDesA0100);
					paralist.add(strDesA0000);
					paralist.add(a0100);
				}
				else
				{
					strsql.append("Insert into ");
					strsql.append(desPre);
					strsql.append(setid);
					strsql.append("(a0100,");
					strsql.append(strfield);
					strsql.append(") select ?,");
					strsql.append(strfield);
					strsql.append(" from ");
					strsql.append(srcPre);
					strsql.append(setid);
					strsql.append(" where a0100=?");
					paralist.add(strDesA0100);
					paralist.add(a0100);				
				}
				dao.update(strsql.toString(),paralist);
			}//for i loop end.
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return strDesA0100;
	}
	/**
	 * 在人员库间移动信息（主集、子集）
	 * @param srcPre 源库前缀
	 * @param desPre 目标库前缀
	 * @param a0100 源库中需要移动的人员的人员编号
	 * @return 移至的目标库的人员编号
	 */
	public String MoveOneEmployee(String srcPre,String desPre,String a0100,boolean bDelete)throws GeneralException
	{
		String strDesA0100=null;
		try
		{
			strDesA0100=CopyOneEmployee(srcPre,desPre,a0100);
			if(!bDelete) {
                updateCreateTime(dao,desPre,strDesA0100);
            } else {
                updateModTime(dao,desPre,strDesA0100);
            }
			/**信息移动时,修改相关的数据,工资,绩效数据,考勤数据*/
			updateA0100(srcPre,desPre,a0100,strDesA0100);
			/**删除人员信息*/
			DeleteOneEmployee(srcPre,desPre,a0100,2);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return strDesA0100;
	}	
}
