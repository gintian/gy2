package com.hjsj.hrms.transaction.info;

import com.hjsj.hrms.valueobject.common.FieldItemView;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class InsertDetailRecordTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ArrayList selfinfolist=(ArrayList)this.getFormHM().get("selectedlist");
		String userbase=(String)this.getFormHM().get("userbase");
		String A0100=(String)this.getFormHM().get("a0100");
		String setname=(String)this.getFormHM().get("setname");
		try{
			 ContentDAO dao=new ContentDAO(this.getFrameconn());
	    	 if(selfinfolist==null||selfinfolist.size()==0)
			 {
				 String i9999=getUserI9999(userbase + setname,A0100,this.getFrameconn());
				 StringBuffer insertSql=new StringBuffer();
				 insertSql.append("insert into ");
				 insertSql.append(userbase + setname);
				 insertSql.append("(a0100,i9999)values('");
				 insertSql.append(A0100);
				 insertSql.append("',");
				 insertSql.append(i9999);
				 insertSql.append(")");
				
				 dao.insert(insertSql.toString(),new ArrayList());
				 saveFiledList("noinfo",A0100,i9999,setname,userbase);
			 }else
			 {
				 RecordVo vo=(RecordVo)selfinfolist.get(0);
				 UpdateHistoryRecord(dao,userbase + setname,A0100,vo.getInt("i9999"));
				 StringBuffer insertSql=new StringBuffer();
				 insertSql.append("insert into ");
				 insertSql.append(userbase + setname);
				 insertSql.append("(a0100,i9999)values('");
				 insertSql.append(A0100);
				 insertSql.append("',");
				 insertSql.append(String.valueOf(vo.getInt("i9999")));
				 insertSql.append(")");				
				 dao.insert(insertSql.toString(),new ArrayList());
				 saveFiledList("noinfo",A0100,String.valueOf(vo.getInt("i9999")),setname,userbase);
				// System.out.println("wlh" + vo.getInt("i9999"));
			 }	 
    	 }catch(Exception e)
    	 {
    		 e.printStackTrace();
  		     throw GeneralExceptionHandler.Handle(e); 
    	 }
	}
	private void UpdateHistoryRecord(ContentDAO dao,String tablename,String a0100,int secn) throws GeneralException
	{
		try{
		   StringBuffer updateSql=new StringBuffer();
		   updateSql.append("Update ");
		   updateSql.append(tablename);
		   updateSql.append(" set i9999=i9999 + 1 where a0100='");
		   updateSql.append(a0100);
		   updateSql.append("' and i9999>=");
		   updateSql.append(secn);
		   dao.update(updateSql.toString());
		}catch(Exception e)
		{
			 e.printStackTrace();
  		     throw GeneralExceptionHandler.Handle(e); 
		}
	}
	private void saveFiledList(String flag,String a0100,String i9999,String setname,String userbase) throws GeneralException
	{
		List infoFieldList=null;
		if("infoself".equalsIgnoreCase(flag)){
		  infoFieldList=userView.getPrivFieldList(setname,0); //获得当前子集的所有属性
		}
		else
		{
		  infoFieldList=userView.getPrivFieldList(setname);   //获得当前子集的所有属性
		}
		List infoFieldViewList=new ArrayList();               //保存处理后的属性
		try
		{
			if(!infoFieldList.isEmpty())
		    {
    			for(int i=0;i<infoFieldList.size();i++)       //字段的集合
				{
					FieldItem fieldItem=(FieldItem)infoFieldList.get(i);				    
					if(fieldItem.getPriv_status() !=0)       //只加在有读写权限的指标
					{
						FieldItemView fieldItemView=new FieldItemView();
						fieldItemView.setAuditingFormula(fieldItem.getAuditingFormula());
						fieldItemView.setAuditingInformation(fieldItem.getAuditingInformation());
						fieldItemView.setCodesetid(fieldItem.getCodesetid());
						fieldItemView.setDecimalwidth(fieldItem.getDecimalwidth());
						fieldItemView.setDisplayid(fieldItem.getDisplayid());
						fieldItemView.setDisplaywidth(fieldItem.getDisplaywidth());
						fieldItemView.setExplain(fieldItem.getExplain());
						fieldItemView.setFieldsetid(fieldItem.getFieldsetid());
						fieldItemView.setItemdesc(fieldItem.getItemdesc());
						fieldItemView.setItemid(fieldItem.getItemid());
						fieldItemView.setItemlength(fieldItem.getItemlength());
						fieldItemView.setItemtype(fieldItem.getItemtype());
						fieldItemView.setModuleflag(fieldItem.getModuleflag());
						fieldItemView.setState(fieldItem.getState());
						fieldItemView.setUseflag(fieldItem.getUseflag());
						fieldItemView.setPriv_status(fieldItem.getPriv_status());
			            //在struts用来表示换行的变量
						fieldItemView.setRowflag(String.valueOf(infoFieldList.size()-1));
						fieldItemView.setValue("");
						infoFieldViewList.add(fieldItemView);
					}
				}				
			}			
		}catch(Exception e){
		   e.printStackTrace();
		   throw GeneralExceptionHandler.Handle(e);
		}finally{		
		   this.getFormHM().put("a0100",a0100);
		   this.getFormHM().put("i9999",i9999);
		   this.getFormHM().put("actiontype","update");
	       this.getFormHM().put("infofieldlist",infoFieldViewList);            //压回页面
       }
    }
    //获得I9999的最大顺序号
	public synchronized String getUserI9999(String strTableName,String userid,Connection conn){
		StringBuffer strsql=new StringBuffer();
		strsql.append("select max(I9999) as I9999 from ");
		strsql.append(strTableName);
		strsql.append(" where a0100");
		strsql.append("='");
		strsql.append(userid);
		strsql.append("'");
		int id=1;
		try
		{
			List rs = ExecuteSQL.executeMyQuery(strsql.toString(),conn);
			if(rs!=null && rs.size()>0)
			{
				LazyDynaBean rec=(LazyDynaBean)rs.get(0);
				id=Integer.parseInt(String.valueOf(rec.get("i9999")!=null&&rec.get("i9999").toString().length()>0?rec.get("i9999"):"0")) + 1;
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
		  //new ExecuteSQL().freeConn();
		}
		return String.valueOf(id);
	}
}
