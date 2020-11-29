package com.hjsj.hrms.transaction.kq.machine.net_signin;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
/*
 * 网上签到 上岛签到 明细
 * wangyao
 */
public class SdSigninListTrans extends IBusiness{

	public void execute() throws GeneralException {
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String nbase =(String)hm.get("dbsign");  //库前缀
		nbase = PubFunc.decrypt(nbase);
		String a0100 = (String)hm.get("a0100sign"); //人员编号
		a0100 = PubFunc.decrypt(a0100);
		String filg =(String)hm.get("filg");
		String sdao_count_field=SystemConfig.getPropertyValue("sdao_count_field"); //得到上岛标识 对应的字段
		sdao_count_field = sdao_count_field.toLowerCase();
//		StringBuffer strsql = new StringBuffer();
//		strsql
//				.append("SELECT kq_year,kq_duration,kq_start,kq_end FROM kq_duration");
//		strsql
//				.append(" where kq_start =(SELECT MIN(kq_start) from kq_duration where finished=0)");
//		strsql
//				.append(" and kq_end =(select min(kq_end) from kq_duration where finished=0)");
		RowSet rowSet=null;
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String start_datesd=null;
		String end_datesd=null;
		String b0110=null;
		String e0122=null;
		String a0101=null;
		try
		{
			if("1".equalsIgnoreCase(filg))
			{
//				rowSet = dao.search(strsql.toString());
//				if(rowSet.next())
//				{
//					start_datesd = rowSet.getString("kq_start");
//					end_datesd = rowSet.getString("kq_end");
//				}
				String registerdate = (String) this.getFormHM().get("registerdate");  //时间
//				start_datesd = start_datesd.substring(0,10);
//				end_datesd = end_datesd.substring(0,10);
				start_datesd = registerdate.replaceAll("-","\\."); //开始
				end_datesd = registerdate.replaceAll("-","\\."); //结束
			}else if("2".equalsIgnoreCase(filg))
			{
				start_datesd = (String) this.getFormHM().get("start_date"); 
				end_datesd = (String) this.getFormHM().get("end_date"); 
				start_datesd = start_datesd.replaceAll("-","\\."); //开始
				end_datesd = end_datesd.replaceAll("-","\\."); //结束
			}
			
			StringBuffer sql2 = new StringBuffer();
			sql2.append("select b0110,e0122,a0101 from q03 where a0100 = '"+a0100+"' and nbase='"+nbase+"' and q03z0='"+end_datesd+"'");
			rowSet = dao.search(sql2.toString());
			if(rowSet.next())
			{
				b0110=rowSet.getString("b0110");
				e0122=rowSet.getString("e0122");
				a0101=rowSet.getString("a0101");
			}
			String sdb0110 = getsdb0110(b0110); //单位
			String sde0122 = getsde0122(e0122); //部门
			StringBuffer sqlsd = new StringBuffer();
			sqlsd.append("q03z0,"+sdao_count_field+"");
			String sql="select "+sqlsd;
			StringBuffer where_str=new StringBuffer();
			where_str.append(" from Q03 where Q03Z0>='"+start_datesd+"' and Q03Z0<='"+end_datesd+"' and ");
			where_str.append("nbase = '"+nbase+"' and a0100 = '"+a0100+"' and "+sdao_count_field+"<>'0'");
			ArrayList fieldlist = DataDictionary.getFieldList("Q03",
					Constant.USED_FIELD_SET);
			ArrayList list = getlist(fieldlist,sdao_count_field);
			
			this.getFormHM().put("sda0101",a0101);
			this.getFormHM().put("sdb0110",sdb0110);
			this.getFormHM().put("sde0122",sde0122);
			this.getFormHM().put("sql_self",sql);
			this.getFormHM().put("where_self",where_str.toString());
			this.getFormHM().put("column_self",sqlsd.toString());
			this.getFormHM().put("order_self","order by Q03Z0");
			this.getFormHM().put("fielditemlist", list);
			this.getFormHM().put("start_date",start_datesd);
			this.getFormHM().put("end_date",end_datesd);
			a0100 = PubFunc.encrypt(a0100);
			nbase = PubFunc.encrypt(nbase);
			this.getFormHM().put("dbsign",nbase);
			this.getFormHM().put("a0100sign",a0100);
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			if(rowSet!=null)
				try {
					rowSet.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
	public ArrayList getlist(ArrayList lists,String str)
	{
		ArrayList list = new ArrayList();
		FieldItem items=null;
		for(int i=0;i<lists.size();i++)
		{
			FieldItem fielditem=(FieldItem)lists.get(i);
			if("q03z0".equalsIgnoreCase(fielditem.getItemid()))
			{
				FieldItem item=(FieldItem)fielditem.clone();	
				item.setViewvalue("q03z0");
				item.setVisible(true);
				list.add(item);
				items=(FieldItem)fielditem.clone();
			}else if(fielditem.getItemid().equalsIgnoreCase(str))
			{
				FieldItem item=(FieldItem)fielditem.clone();
				item.setViewvalue("sd");
				item.setVisible(true);
				list.add(item);
				items.setViewvalue("sdvalue");
				items.setVisible(true);
				list.add(items);
			}
		}
		return list;
	}
	/*
	 * 得到单位
	 */
	public String getsdb0110(String b0110)
	{
		String sdb0110="";
		StringBuffer sql = new StringBuffer();
		sql.append("select codeitemdesc from organization where codeitemid='"+b0110+"'");
		RowSet rowSet=null;
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try
		{
			rowSet = dao.search(sql.toString());
			if(rowSet.next())
			{
				sdb0110 = rowSet.getString("codeitemdesc");
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
        	if(rowSet!=null)
				try {
					rowSet.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        }
		return sdb0110;
	}
	/**
	 * 得到部门
	 * @param b0110
	 * @return
	 */
	public String getsde0122(String e0122)
	{
		String sde0122="";
		StringBuffer sql = new StringBuffer();
		sql.append("select codeitemdesc from organization where codeitemid='"+e0122+"'");
		RowSet rowSet=null;
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try
		{
			rowSet = dao.search(sql.toString());
			if(rowSet.next())
			{
				sde0122 = rowSet.getString("codeitemdesc");
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
        	if(rowSet!=null)
				try {
					rowSet.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        }
		return sde0122;
	}
}
