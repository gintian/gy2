package com.hjsj.hrms.transaction.general.statics.crossstatic;

import com.hjsj.hrms.businessobject.sys.logonuser.UserObjectBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * 检测二维交叉表要保持的参数是否合法
 * 
 * <p>Title: CheckTowCrossInfoTrans </p>
 * <p>Company: hjsj</p>
 * <p>create time  Sep 13, 2014 2:08:48 PM</p>
 * @author liuy
 * @version 1.0
 */
public class CheckTowCrossInfoTrans extends IBusiness{

	public void execute() throws GeneralException {
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String crosswise = (String)this.getFormHM().get("crosswise");//横向维度
		String lengthways = (String)this.getFormHM().get("lengthways");//纵向维度
		String crossname = (String)this.getFormHM().get("crossname");//统计名称
		String type = (String)this.getFormHM().get("type");//分类
		String dbname = (String)this.getFormHM().get("dbname");//人员库
		String condition = (String)this.getFormHM().get("condition");//分类统计条件
		String hiderow = (String)this.getFormHM().get("hiderow");//隐藏空行
		String hidecol = (String)this.getFormHM().get("hidecol");//隐藏空列
		String crosswiseTotal = (String)this.getFormHM().get("crosswiseTotal");//横向合计
		String lengthwaysTotal = (String)this.getFormHM().get("lengthwaysTotal");//纵向合计
		String showChart = (String)this.getFormHM().get("showChart");//显示统计图
		
		String HV = "";
		HV = getHV(dao, crosswise, lengthways);
		
		String infomsg = "";
		crossname = crossname.trim();
		dbname = dbname.trim();
		condition = condition.trim();
		
		ArrayList list = new ArrayList();
		list=this.getNameList(dao);
		for(int i=0;i<list.size();i++){
			if(list.get(i).equals(crossname)){
				infomsg="名称不能重复！";
				break;
			}
		}
		if(infomsg==""){
			try
			{
				String id = this.getMaxId();
				String snorder = this.getMaxSnorder();
				ArrayList paralist=new ArrayList();	
				StringBuffer sqlstr = new StringBuffer();
				sqlstr.append("insert into SName (Id,Flag,InfoKind,snorder,Type,Name,categories,nbase,condid,HV,hide_empty_row,hide_empty_col,show_chart,show_sum_h,show_sum_v) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				paralist.add(new Integer(id));
				paralist.add(new Integer("0"));
				paralist.add(new Integer("1"));
				paralist.add(new Integer(snorder));
				paralist.add(new Integer("3"));
				paralist.add(crossname);
				paralist.add(type);
				paralist.add(dbname);
				paralist.add(condition);
				paralist.add(HV);
				paralist.add(new Integer(hiderow));
				paralist.add(new Integer(hidecol));
				paralist.add(new Integer(showChart));
				paralist.add(new Integer(crosswiseTotal));
				paralist.add(new Integer(lengthwaysTotal));
				
				dao.insert(sqlstr.toString(),paralist);
				UserObjectBo user_bo = new UserObjectBo(this.getFrameconn());
				user_bo.saveResource(id, this.userView,IResourceConstant.STATICS);
			}
			catch(Exception e)
			{
				e.printStackTrace();
				infomsg = "保存失败！";
			}
		}
		this.getFormHM().put("infomsg", infomsg);
		
	}
	
	private ArrayList getNameList(ContentDAO dao){
		ResultSet rs = null;
		ArrayList nameList = new ArrayList();
		String dbsql = "select Name from SName";
		try {
			rs = dao.search(dbsql);
			while(rs.next()) {
				nameList.add(rs.getString("Name"));
			}
		} catch(Exception e) {
			e.printStackTrace();
		}finally
		{
			try {
				if(rs!=null){
					rs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return nameList;
	}
	/**
	 * 得到最大的Id
	 * @return
	 * @throws GeneralException
	 */
	private String getMaxId()throws GeneralException {
		int nid=-1;
		StringBuffer sql=new StringBuffer("select max(id)+1 as nmax from sname");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
			this.frowset=dao.search(sql.toString());
			if(this.frowset.next())
			{
				nid=this.frowset.getInt("nmax");
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
   	       throw GeneralExceptionHandler.Handle(ex);			
		}
		return String.valueOf(nid);
	}
	
	/**
	 * 得到最大的snorder
	 * @return
	 * @throws GeneralException
	 */
	private String getMaxSnorder()throws GeneralException {
		int snorder=-1;
		StringBuffer sql=new StringBuffer("select max(snorder)+1 as smax from sname");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
			this.frowset=dao.search(sql.toString());
			if(this.frowset.next())
			{
				snorder=this.frowset.getInt("smax");
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
   	       throw GeneralExceptionHandler.Handle(ex);			
		}
		return String.valueOf(snorder);
	}
	
	/**
	 * 得到选中的横纵维度
	 * @param dao
	 * @param crosswise
	 * @param lengthways
	 * @return
	 */
	private String getHV(ContentDAO dao, String crosswise ,String lengthways) {
		String hv ="";
		String[] temp;
		String crosswiseHV="",lengthwaysHV="";
		temp = crosswise.split(",");
		for (int i = temp.length-1; i >= 0; i--) {
			crosswiseHV+= temp[i];
		}
		crosswiseHV=crosswiseHV.replaceAll("1_", ";");
		crosswiseHV=crosswiseHV.replaceAll("2_", ",");
		if(crosswiseHV.indexOf(";")==0){
			crosswiseHV=crosswiseHV.substring(1,crosswiseHV.length());
		}
		temp = lengthways.split(",");
		for (int i = temp.length-1; i >= 0; i--) {
			lengthwaysHV+= temp[i];
		}
		lengthwaysHV=lengthwaysHV.replaceAll("1_", ";");
		lengthwaysHV=lengthwaysHV.replaceAll("2_", ",");
		if(lengthwaysHV.indexOf(";")==0){
			lengthwaysHV=lengthwaysHV.substring(1,lengthwaysHV.length());
		}
		hv=crosswiseHV+"|"+lengthwaysHV;
		return hv;
	}
}
