package com.hjsj.hrms.transaction.orginfo;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

public class AssaveFieldItemTrans  extends IBusiness {


	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String setname=(String)this.getFormHM().get("setname");
		String assavefields=(String)this.getFormHM().get("assavefields");
		if(setname==null||setname.length()<=0)
			throw GeneralExceptionHandler.Handle(new Exception("没有得到子集相关信息！"));
		if(assavefields==null||assavefields.length()<=0)
			throw GeneralExceptionHandler.Handle(new Exception("没有得到子集批量另存指标信息！"));
		ArrayList selfinfolist=(ArrayList)this.getFormHM().get("selectedlist");
		if(selfinfolist==null||selfinfolist.size()==0)
	            return;
		FieldSet fieldset=DataDictionary.getFieldSetVo(setname); 
		String changeflag=fieldset.getChangeflag();//0为一般子集（没有z0，z1），1为按月，2按年变化子集有（z0，z1）
		String arrfields[]=assavefields.split(",");
		ArrayList fieldlist=new ArrayList();
		for(int r=0;r<arrfields.length;r++)
		{
			if(arrfields[r]!=null&&arrfields[r].length()>0)
			{
				FieldItem item=DataDictionary.getFieldItem(arrfields[r]);
				if(item!=null)
					fieldlist.add(item);
			}
		}
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		for(int i=0;i<selfinfolist.size();i++)
		{
			 RecordVo vo=(RecordVo)selfinfolist.get(i);
			 RecordVo vonew=new RecordVo(setname);		
			 String b0110=vo.getString("b0110");
			 int i9999=vo.getInt("i9999");
			 vonew.setString("b0110", b0110);
			 int maxi9999=getI9999(setname,b0110,dao);
			 vonew.setInt("i9999", (maxi9999+1));
			 vonew.setDate("createtime".toLowerCase(), PubFunc.getStringDate("yyyy-MM-dd hh:mm:ss"));
			 vonew.setDate("modtime".toLowerCase(), PubFunc.getStringDate("yyyy-MM-dd hh:mm:ss"));
			 vonew.setString("createusername".toLowerCase(), userView.getUserName());
			 vonew.setString("modusername".toLowerCase(), null);
			 String appdate=ConstantParamter.getAppdate(this.userView.getUserName());
			 Date date=DateUtils.getDate(appdate,"yyyy.MM.dd");
			 if(!"0".equals(changeflag))
				 vonew.setDate((setname+"z0").toLowerCase(),date);
			 RecordVo oldVo=findAsFieldItem(b0110,i9999,setname,dao);
			 for(int r=0;r<fieldlist.size();r++)
		     {
				 FieldItem item=(FieldItem)fieldlist.get(r);
				 if("D".equals(item.getItemtype()))                               //日期类型的有待格式化处理
		     	 {
					 vonew.setDate(item.getItemid(), oldVo.getDate(item.getItemid()));
		     	 }else if("N".equals(item.getItemtype()))                        //数值类型的
		     	 {
		     		//【55670】批量另存时没有数据的数值类型指标，直接赋值为null
		     		if(StringUtils.isEmpty(oldVo.getString(item.getItemid()))) {
		     			vonew.setString(item.getItemid(), null);
		     		} else if(item.getDecimalwidth()>0) {
		     			vonew.setDouble(item.getItemid(), oldVo.getDouble(item.getItemid()));
		     		} else {
		     			vonew.setInt(item.getItemid(), oldVo.getInt(item.getItemid()));
		     		}
		     	 }else{
		     		vonew.setString(item.getItemid(), oldVo.getString(item.getItemid()));
		     	 }
		     }
			 if(!"0".equals(changeflag))
				 vonew.setInt((setname+"z1").toLowerCase(),oldVo.getInt((setname+"z1").toLowerCase()));
			 dao.addValueObject(vonew);
		}
	}
	public RecordVo findAsFieldItem(String b0110,int i9999,String setname,ContentDAO dao)throws GeneralException
	{
		 RecordVo vo=new RecordVo(setname);
		 vo.setString("b0110",b0110);
		 vo.setInt("i9999",i9999);
		 try {
			vo=dao.findByPrimaryKey(vo);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return vo;
	}
	private int getI9999(String setname,String b0110,ContentDAO dao)
	{
		String sql="select max(i9999) i9999 from "+setname+" where b0110='"+b0110+"'";
		RowSet rs=null;
		int i9999=0;
		try {
			rs=dao.search(sql);
			if(rs.next())
				i9999=rs.getInt("i9999");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally
		{
			if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return i9999;
		
	}
}
