package com.hjsj.hrms.transaction.hire.jp_contest.personinfo;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
/**
 * 
 *<p>Title:ApproveApp.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Dec 22, 2007</p> 
 *@author huaitao
 *@version 4.0
 */
public class ApproveApp extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String template = (String)this.getFormHM().get("tamplate");
		ArrayList selectedlist = (ArrayList)this.getFormHM().get("selectedlist");
		ArrayList list = new ArrayList();
		for(int i=0;i<selectedlist.size();i++){
			LazyDynaBean bean = (LazyDynaBean)selectedlist.get(i);
			String a0100 = (String)bean.get("a0100");
			String z0700 = (String)bean.get("zp_z0700");
			ArrayList templist = new ArrayList();
			templist.add("03");
			templist.add(a0100);
			templist.add(z0700);
			list.add(templist);
		}
		String sql = "update zp_apply_jobs set state=? where a0100 =? and z0700 =?";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try 
		{
			dao.batchUpdate(sql,list);
			/**发送消息*/
			String[] templates=StringUtils.split(template, ",");			
			for(int i=0;i<selectedlist.size();i++){
				LazyDynaBean bean = (LazyDynaBean)selectedlist.get(i);
				String a0100 = (String)bean.get("a0100");
				String nbase = (String)bean.get("nbase");
				String posid=(String)bean.get("zp_z0700");
				String poscode=getJpname(posid);
				sendMessage(nbase,a0100,poscode,templates);
			}

		}
		catch (Exception  e) 
		{
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	/**
	 * @param NBase 		人员库前缀
	 * @param a0100			人员编码
	 * @param poscode		调整职位
	 * @param templates　	模板号列表
	 * @throws GeneralException
	 */
	private void sendMessage(String NBase,String a0100,String poscode,String[] templates)throws GeneralException
	{
		if(templates==null||templates.length==0)
			return;
		if(poscode==null||poscode.length()==0)
			return;
		StringBuffer strlast=new StringBuffer();
		StringBuffer strpre=new StringBuffer();
		StringBuffer strchg=new StringBuffer();
		StringBuffer buf=new StringBuffer();
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		int nyear=0;
		int nmonth=0;
		nyear=DateUtils.getYear(new Date());
		nmonth=DateUtils.getMonth(new Date());		
		try
		{
			
			String tablename=NBase+"A01";
			RecordVo srcvo=new RecordVo(tablename);
			srcvo.setString("a0100", a0100);
			srcvo=dao.findByPrimaryKey(srcvo);
			if(srcvo==null)
				return;
			CodeItem item=AdminCode.getCode("@K", poscode);
			if(item==null)
				return;
			String e0122=item.getPcodeitem();
			String b0110=DbNameBo.findUnitCodeByDeptId(e0122);
			for(int i=0;i<templates.length;i++)
			{
				String dest_id=templates[i];
				strlast.setLength(0);
				strpre.setLength(0);

				strchg.setLength(0);
				strchg.append("B0110,E0122,E01A1,");
				strlast.append("B0110=");
				strlast.append(b0110);
				strlast.append(",");
				strlast.append("E0122=");
				strlast.append(e0122);
				strlast.append(",");
				strlast.append("E01A1=");
				strlast.append(poscode);
				strlast.append(",");				
				
				RecordVo vo=new RecordVo("tmessage");
				vo.setInt("state",0);
				vo.setString("b0110",this.getUserView().getUserOrgId());
				vo.setInt("nyear",nyear);
				vo.setInt("nmonth",nmonth);
				vo.setInt("type",0);
				vo.setInt("flag",0);
				vo.setString("a0100",a0100);
				vo.setString("db_type",NBase);  //主要为了移库操作						
				vo.setString("a0101",srcvo.getString("a0101"));
				vo.setInt("sourcetempid",0);//直接来自于其它业务，不是通过模板之间发送消息.
				vo.setInt("noticetempid",Integer.parseInt(dest_id));
				
				vo.setString("changepre",strpre.toString());
				vo.setString("changelast",strlast.toString());
				vo.setString("change",strchg.toString());
				/**max id access mssql此字段是自增长类型*/
				if(Sql_switcher.searchDbServer()!=Constant.MSSQL)
				{
					int nid=DbNameBo.getPrimaryKey("tmessage", "id", this.getFrameconn());
					vo.setInt("id", nid);
				}
				dao.addValueObject(vo);
			} //for i loop end.
			
		}
		catch(Exception ex)
		{
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	
	/**
	 * 得到表Z07中竞聘岗位z0701名称
	 * @param id z0700
	 * @return
	 */
	private String getJpname(String id){
		String value = "";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String sql = "select z0701 from z07 where z0700 = "+id;
		try {
			RowSet rs = dao.search(sql);
			while(rs.next()){
				value = rs.getString("z0701");
			}
		} catch (SQLException e) {e.printStackTrace();}
		return value;
	}
}
