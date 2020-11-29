package com.hjsj.hrms.transaction.kq.machine.historical;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.machine.KqCardData;
import com.hjsj.hrms.businessobject.kq.machine.ReconstructionKqField;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.kq.team.GroupsArray;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
/**
 * 刷卡数据初始值
 * <p>Title:InitCataDataTrans.java</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Dec 20, 2006 10:07:28 AM</p>
 * @author sunxin
 * @version 1.0
 *
 */
public class InitCataDataTrans extends IBusiness{
    public void execute()throws GeneralException
    {
    	String a_code=(String)this.getFormHM().get("a_code");
    	DbWizard dbw = new DbWizard(this.getFrameconn());
    	if(!dbw.isExistTable("kq_originality_data_arc", false))
    		throw GeneralExceptionHandler.Handle(new GeneralException("","刷卡数据未归档！","",""));
    	cheakOriginality_data();//重构数据
    	KqCardData kqCardData=new KqCardData(this.userView,this.getFrameconn());
    	ArrayList kq_dbase_list=(ArrayList)this.getFormHM().get("kq_dbase_list");
    	String error_message="";
		String error_flag="0";
		String error_return="";
		String error_stuts=(String)this.getFormHM().get("error_stuts");		
	    String start_date=(String)this.getFormHM().get("start_date");
	    String start_hh=(String)this.getFormHM().get("start_hh");
	    String start_mm=(String)this.getFormHM().get("start_mm");
	    String end_date=(String)this.getFormHM().get("end_date");
	    String end_hh=(String)this.getFormHM().get("end_hh");
	    String end_mm=(String)this.getFormHM().get("end_mm");	 
		if(error_stuts!=null&& "1".equals(error_stuts))
		{
			error_flag=(String)this.getFormHM().get("error_flag");
			error_return=(String)this.getFormHM().get("error_return");
		}
    	if(a_code==null||a_code.length()<=0)
    	{
//    		a_code=kqCardData.getACode();   //更改为 查询出当前组织权限 
    		a_code=kqCardData.getACodefull();
    	}    	
    	String codeid="";
    	if(a_code!=null&&a_code.length()>2)
    	{
    		codeid=a_code.substring(2);
    	}   
    	if(a_code.indexOf("EP")!=-1)
    	{
    		codeid=RegisterInitInfoData.getKqPrivCodeValue(this.userView);
    	}
    	ArrayList datelist=(ArrayList)this.getFormHM().get("datelist");
    	if(datelist==null||datelist.size()<=0)
    	{
    		datelist=RegisterDate.registerdate(codeid,this.getFrameconn(),this.userView); 
    	}   
    	/*if(datelist==null||datelist.size()<=0)
		{
    		if(start_date==null||start_date.length()<=0)
				start_date=PubFunc.getStringDate("yyyy.MM.dd");	
			if(end_date==null||end_date.length()<=0)
				end_date=PubFunc.getStringDate("yyyy.MM.dd");	
		}else
		{
			 CommonData vo=(CommonData)datelist.get(0);	  	  
		     if(start_date==null||start_date.length()<=0)
				start_date=vo.getDataValue();	
			 if(end_date==null||end_date.length()<=0)
				end_date=vo.getDataValue();	
		}*/
    	if(datelist!=null&&datelist.size()>0){
    		CommonData cd=(CommonData)datelist.get(0);
    		String dd=cd.getDataValue();
    		dd=dd.replaceAll("\\.", "\\-");
    		dd=DateUtils.format(DateUtils.addMonths(DateUtils.getDate(dd, "yyyy-MM-dd"), -2),"yyyy-MM-dd");;
    		start_date=dd;
    		end_date=dd;
    	}
    	if(start_date==null||start_date.length()<=0)
			start_date=PubFunc.getStringDate("yyyy.MM.dd");	
		if(end_date==null||end_date.length()<=0)
			end_date=PubFunc.getStringDate("yyyy.MM.dd");	
    	if(start_hh==null||start_hh.length()<=0)
			start_hh="00";
		if(start_mm==null||start_mm.length()<=0)
			start_mm="00";
		
		if(end_hh==null||end_hh.length()<=0)
			end_hh="23";
		if(end_mm==null||end_mm.length()<=0)
			end_mm="59";
    	String code_kind=(String)this.getFormHM().get("code_kind");
    	String group_OrgId="";
    	if(a_code.toUpperCase().indexOf("UN")!=-1)
    	{
    		code_kind=RegisterInitInfoData.getDbB0100(codeid,"2",this.getFormHM(),this.userView,this.getFrameconn());
    	}else if(a_code.toUpperCase().indexOf("UM")!=-1)
    	{
    		code_kind=RegisterInitInfoData.getDbB0100(codeid,"1",this.getFormHM(),this.userView,this.getFrameconn());
    	}else if(a_code.toUpperCase().indexOf("@K")!=-1)
    	{
    		code_kind=RegisterInitInfoData.getDbB0100(codeid,"0",this.getFormHM(),this.userView,this.getFrameconn());
    	}else if(a_code.indexOf("GP")!=-1)
    	{
    		GroupsArray groupsArray=new GroupsArray(this.getFrameconn(),this.userView);
    		String org_id=groupsArray.getCodeFromGroupId(codeid,this.getFrameconn());
    		if(org_id.indexOf("UN")!=-1||org_id.indexOf("UM")!=-1||org_id.indexOf("@K")!=-1)
    			org_id=org_id.substring(2);
    		group_OrgId=org_id;
    		//if(org_id!=null&&org_id.length()>0)
    		   code_kind=RegisterInitInfoData.getDbB0100(org_id,"0",this.getFormHM(),this.userView,this.getFrameconn());
    	}   
    	/****得到人员库****/
    	if(codeid!=null&&codeid.length()>0)
		{
    			if(a_code.indexOf("UN")!=-1)
    			{
    				kq_dbase_list=RegisterInitInfoData.getB0110Dase(this.getFormHM(),this.userView,this.getFrameconn(),codeid);
    			}else if(a_code.indexOf("UM")!=-1||a_code.indexOf("@K")!=-1)
    			{
    				String b0110=codeid;
    				String codesetid=codeid;
    	        	do
    	        	{
    	        		String codeset[]=RegisterInitInfoData.getB0100(b0110,this.getFrameconn());
    	        		if(codeset!=null&&codeset.length>=0)
    	            	{
    	            		codesetid=codeset[0];
    	            		b0110=codeset[1];
    	            	}
    	        	}while(!"UN".equals(codesetid));
    	        	kq_dbase_list=RegisterInitInfoData.getB0110Dase(this.getFormHM(),this.userView,this.getFrameconn(),b0110);
    			}else if(a_code.indexOf("EP")!=-1)
    			{
    				String  nbase=(String)this.getFormHM().get("nbase");
    				ArrayList list=new ArrayList();
    				list.add(nbase);
    				kq_dbase_list=list;
    			}else if(a_code.indexOf("GP")!=-1)
    			{
    				kq_dbase_list=RegisterInitInfoData.getDase3(this.getFormHM(),this.userView,this.getFrameconn()); 
    			}
		}else{
			 kq_dbase_list=RegisterInitInfoData.getDase3(this.getFormHM(),this.userView,this.getFrameconn()); 
		} 
    	if(kq_dbase_list==null||kq_dbase_list.size()<=0)
    		throw GeneralExceptionHandler.Handle(new GeneralException("","没有定义人员库","",""));
    	boolean isInout_flag=kqCardData.isViewInout_flag();
    	ArrayList fielditemlist=kqCardData.machineDataFieldlist1(isInout_flag);
    	/**对比Q03隐藏指标,如果Q03隐藏刷卡数据也隐藏 wangy**/
		ArrayList fieldlist = DataDictionary.getFieldList("Q03",
				Constant.USED_FIELD_SET);
		ArrayList fielditemlistQ03= RegisterInitInfoData.newFieldItemList(fieldlist,this.userView,this.frameconn);
		String a0101zx="false";
		String b0110zx="false";
		String e0122zx="false";
		for(int p=0;p<fielditemlistQ03.size();p++)
		{
			FieldItem fielditem=(FieldItem)fielditemlistQ03.get(p);
//			System.out.println("kk = "+fielditem.getItemid());
			if("a0101".equalsIgnoreCase(fielditem.getItemid()))
			{
				if("1".equals(fielditem.getState()))
				{
					a0101zx="true";
//					System.out.println("姓名 = "+a0101zx);
				}
			}else if("b0110".equalsIgnoreCase(fielditem.getItemid()))
			{
				if("1".equals(fielditem.getState()))
				{
					b0110zx="true";
//					System.out.println("单位 = "+b0110zx);
				}
			}else if("e0122".equalsIgnoreCase(fielditem.getItemid()))
			{
				if("1".equals(fielditem.getState()))
				{
					e0122zx="true";
//					System.out.println("部门 = "+e0122zx);
				}
			}
		}
		String kqj=getkqj();  //控制 考勤机号 是否展现
		
    	/**结束**/
        this.getFormHM().put("fielditemlist",fielditemlist);
        this.getFormHM().put("isInout_flag", isInout_flag+"");
    	this.getFormHM().put("a_code",a_code);
    	this.getFormHM().put("start_date",start_date);
    	this.getFormHM().put("start_hh",start_hh);
    	this.getFormHM().put("start_mm",start_mm);
    	this.getFormHM().put("end_date",end_date);
    	this.getFormHM().put("end_hh",end_hh);
    	this.getFormHM().put("end_mm",end_mm);
    	this.getFormHM().put("kq_dbase_list",kq_dbase_list);
    	KqUtilsClass kqUtilsClass=new KqUtilsClass(this.getFrameconn());
    	this.getFormHM().put("kq_list",kqUtilsClass.getKqNbaseListNullAll(kq_dbase_list));
    	/***error message***/
    	this.getFormHM().put("error_message",error_message);
		this.getFormHM().put("error_flag",error_flag);
		this.getFormHM().put("error_return",error_return);
		this.getFormHM().put("code_kind",code_kind);
		/**Q03是否隐藏**/
		this.getFormHM().put("a0101zx",a0101zx);
		this.getFormHM().put("b0110zx",b0110zx);
		this.getFormHM().put("e0122zx",e0122zx);
		this.getFormHM().put("kqj",kqj);
		String sync_carddata=SystemConfig.getPropertyValue("syncKqcardData");//同步考勤刷卡数据
	    sync_carddata=sync_carddata!=null&&sync_carddata.length()>0?sync_carddata:"false";
	    this.getFormHM().put("sync_carddata",sync_carddata);
    }
    /**
     * 重构数据
     *
     */
    private void cheakOriginality_data()throws GeneralException
    {
    	ReconstructionKqField reconstructionKqField=new ReconstructionKqField(this.getFrameconn());
    	ArrayList list=new ArrayList();
    	Field temp = new Field("inout_flag","出入标志");
		temp.setDatatype(DataType.INT);	
		temp.setKeyable(false);			
		temp.setVisible(false);
		list.add(temp);
		temp = new Field("oper_cause","补刷原因");
		temp.setDatatype(DataType.STRING);
		temp.setKeyable(false);			
		temp.setVisible(false);
		temp.setLength(50);
		list.add(temp);
		temp = new Field("oper_user","补刷操作员");
		temp.setDatatype(DataType.STRING);
		temp.setKeyable(false);			
		temp.setVisible(false);
		temp.setLength(50);
		list.add(temp);
		temp = new Field("oper_time","补刷时间");
		temp.setDatatype(DataType.DATE);
		temp.setLength(16);
		temp.setKeyable(false);			
		temp.setVisible(false);		
		list.add(temp);
		temp = new Field("oper_mach","机器ip或机器名");
		temp.setDatatype(DataType.STRING);
		temp.setKeyable(false);			
		temp.setVisible(false);
		temp.setLength(50);
		list.add(temp);
		if(!reconstructionKqField.checkFieldSave("kq_originality_data_arc","inout_flag"))
		{
			if(!reconstructionKqField.ceaterField_originality(list,"kq_originality_data_arc"))
				throw GeneralExceptionHandler.Handle(new GeneralException("","刷卡数据未归档！","","")); 
		}
		list=new ArrayList();
    	temp = new Field("sp_flag","审批标志");
		temp.setDatatype(DataType.STRING);	
		temp.setLength(2);
		temp.setKeyable(false);			
		temp.setVisible(false);
		list.add(temp);
		if(!reconstructionKqField.checkFieldSave("kq_originality_data_arc","sp_flag"))
		{
			if(!reconstructionKqField.ceaterField_originality(list,"kq_originality_data_arc"))
				throw GeneralExceptionHandler.Handle(new GeneralException("","刷卡数据未归档！","","")); 
			String upSQL="update kq_originality_data_arc set sp_flag='03' where sp_flag is null";
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			try
			{
				dao.update(upSQL);
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		list=new ArrayList();
    	temp = new Field("sp_user","审批人");
		temp.setDatatype(DataType.STRING);	
		temp.setLength(50);
		temp.setKeyable(false);			
		temp.setVisible(false);
		list.add(temp);
		temp = new Field("sp_time","审批日期");
		temp.setDatatype(DataType.DATETIME);			
		temp.setKeyable(false);			
		temp.setVisible(false);
		list.add(temp);
		if(!reconstructionKqField.checkFieldSave("kq_originality_data_arc","sp_user"))
		{
			if(!reconstructionKqField.ceaterField_originality(list,"kq_originality_data_arc"))
				throw GeneralExceptionHandler.Handle(new GeneralException("","没有刷卡归档数据！","","")); 			
		}
    }
    /*
     * 判断考勤机字段是否展现 wangy
     */
    public String getkqj()
    {
    	String flag="false";
    	ContentDAO dao=new ContentDAO(this.getFrameconn());
    	StringBuffer sql = new StringBuffer();
    	RowSet rowSet=null;
    	sql.append("select * from kq_machine_location where 1=1");
    	try
    	{
    		rowSet=dao.search(sql.toString());
    		if(rowSet.next())
    		{
    			flag="true";
    			return flag;
    		}
    	}catch(Exception e)
    	{
    		flag="false";
    		e.printStackTrace();
    	}finally
    	{
    		if(rowSet!=null)
				try {
					rowSet.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    	}
    	return flag;
    }
}
