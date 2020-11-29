package com.hjsj.hrms.transaction.kq.register.historical;

import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hjsj.hrms.businessobject.kq.register.HistoryBrowse;
import com.hjsj.hrms.businessobject.kq.register.OrgRegister;
import com.hjsj.hrms.businessobject.kq.register.history.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.history.RegisterInitInfoData;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

public class SumOrgBrowseTrans extends IBusiness {
	public void execute() throws GeneralException 
	{
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String year=(String)this.getFormHM().get("year");//考勤期间
		String duration=(String)this.getFormHM().get("duration");	
		String code=(String)this.getFormHM().get("code");
		String kind=(String)this.getFormHM().get("kind");
		ArrayList yearlist=(ArrayList)this.getFormHM().get("yearlist");
		ArrayList durationlist=(ArrayList)this.getFormHM().get("durationlist");
		ArrayList kq_dbase_list = (ArrayList)this.getFormHM().get("kq_dbase_list");
		//转换小时 1=默认；2=HH:MM
		String selectys=(String)hm.get("selectys");
		if(selectys==null|| "".equals(selectys))
		{
			selectys="1";
		}
		this.getFormHM().put("selectys",selectys);
		
		if(kq_dbase_list==null||kq_dbase_list.size()<=0)
		{
			kq_dbase_list=userView.getPrivDbList();  
		}
		String cur_year="";
		String cur_duration="";
		yearlist=RegisterDate.yearDate(this.frameconn,"1");
		if(yearlist!=null&&yearlist.size()>0)
		{			
			if(year!=null&&year.length()>0)
			{
				cur_year=year;
			}else{
				CommonData vy = (CommonData) yearlist.get(0);
				cur_year=vy.getDataValue();
			}			
		}else{
				
			  throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.session.nohistory"),"",""));
		}
		
		durationlist=RegisterDate.durationDate(this.frameconn, "1", cur_year);
		if(durationlist!=null&&durationlist.size()>0)
		{			
			if(duration!=null&&duration.length()>0)
			{
				cur_duration=duration;
			}else{
				CommonData vd = (CommonData) durationlist.get(0);
				cur_duration=vd.getDataValue();
			}			
		}else{
				
			  throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.session.nohistory"),"",""));
		}
		if(code==null||code.length()<=0){
			code="";
		}
		String b0110="";
		if(code.length()<=0){
			ManagePrivCode managePrivCode=new ManagePrivCode(userView,this.getFrameconn());			
			code=managePrivCode.getPrivOrgId();
			b0110="UN"+code;
		}else{
			b0110="UN"+code;
				
		}
		if(kind==null||kind.length()<=0)
		{
			kind="2";
		}
		ArrayList datelist=HistoryBrowse.registerdate(b0110,this.getFrameconn(),this.userView,cur_year+"-"+cur_duration,"1");
		String start_date="";
		String end_date="";
		if(datelist!=null&&datelist.size()>0)
		{
				CommonData vo = (CommonData) datelist.get(0);				
				start_date=vo.getDataValue();
				vo = (CommonData) datelist.get(datelist.size()-1);
				end_date=vo.getDataValue();
		}
		 ArrayList fielditemlist = DataDictionary.getFieldList("Q03",
					Constant.USED_FIELD_SET);
			 ArrayList list= OrgRegister.newFieldItemList(fielditemlist);
			 String codesetid="UN";
			 if(!userView.isSuper_admin()) 
	        {
			 if("UM".equals(RegisterInitInfoData.getKqPrivCode(userView)))
				codesetid="UM";	
	        }
			 list=OrgRegister.newFieldItemListQ09(list,codesetid);
			 ArrayList a0100whereIN= new ArrayList();
			 for(int i=0;i<kq_dbase_list.size();i++)
			 {
					String dbase=kq_dbase_list.get(i).toString();
					String whereA0100In=RegisterInitInfoData.getWhereINSql(this.userView,dbase);
					a0100whereIN.add(whereA0100In);
			 }
			 String whereE0122=OrgRegister.selcet_kq_AllOrgId("e0122",a0100whereIN,code);
			 ArrayList orgide0122List=OrgRegister.getQrgE0122List(this.frameconn,whereE0122,"e0122");
			 StringBuffer b0110Str=new StringBuffer();				 
			 for(int i=0;i<orgide0122List.size();i++)
			 {
					b0110Str.append("'"+orgide0122List.get(i).toString()+"',");
			 }			 
			 if(code!=null&&code.length()>0)
			 {
					b0110Str.append("'"+code+"',");
			 }
			 String b0100s="";
			 if(b0110Str.toString()!=null&&b0110Str.toString().length()>0)
			 {
				 b0100s= b0110Str.toString().substring(0,b0110Str.length()-1);	
			 }else
			 {
				 throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.date.no.record"),"",""));
			 }
				DbWizard dbWizard =new DbWizard(this.getFrameconn());
				ArrayList sqllist=null;
				if(!dbWizard.isExistTable("Q09_arc", false)){
					throw new GeneralException("无归档数据！");
				}else{
					sqllist=OrgRegister.getSqlstrHistory(list,b0100s,cur_year+"-"+cur_duration, b0110, "Q09_arc");
				}
		this.getFormHM().put("sqlstr", sqllist.get(0).toString());
  		this.getFormHM().put("strwhere", sqllist.get(1).toString());
  		this.getFormHM().put("orderby", sqllist.get(2).toString());
		this.getFormHM().put("columns", sqllist.get(3).toString());	
		this.getFormHM().put("condition",SafeCode.encode("9`"+sqllist.get(4).toString()));
		this.getFormHM().put("relatTableid","9");
		this.getFormHM().put("returnURL","/kq/register/history/sumorgbrowsedata.do?b_search=link");
		this.getFormHM().put("fielditemlist", list);
		this.getFormHM().put("kq_dbase_list",kq_dbase_list);
		this.getFormHM().put("code",code);
		this.getFormHM().put("kind",kind);
		this.getFormHM().put("yearlist",yearlist);
		this.getFormHM().put("durationlist",durationlist);
		this.getFormHM().put("datelist",datelist);		
		this.getFormHM().put("year",cur_year);
		this.getFormHM().put("duration",cur_duration);		
		this.getFormHM().put("start_date",start_date);
		this.getFormHM().put("end_date",end_date);
		/**部门历史数据 月汇总 
		 * hmusterlist!=null 展现打印功能
		 * **/
		//SelectHmusterNameTrans  Select = new SelectHmusterNameTrans();
		ArrayList hmusterlist = new ArrayList();
	    hmusterlist=/*Select.*/getKQ_GZMusterList("9","81");
	    String flag="";
	    if(hmusterlist.size()>0)
	    {
	    	flag="1";
	    }else
	    {
	    	flag="0";
	    }
	    this.getFormHM().put("flag", flag);
	    /**结束**/
	}


	/**
	 * 取得考勤高级花名册信息列表
	 * @param relatTabid
	 * @return
	 */
	public ArrayList getKQ_GZMusterList(String relatTabid,String nFlag)
	{
		ArrayList kq_musterList=new ArrayList();
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try
		{
			if("81".equals(nFlag))
				this.frowset=dao.search("select tabid,cname from muster_name where nModule=81 and nPrint="+relatTabid);
			else if("5".equals(nFlag))
				this.frowset=dao.search("select tabid,cname from muster_name where nModule=5 and nPrint="+relatTabid);
			while (this.frowset.next()) {
				if(!this.getUserView().isHaveResource(IResourceConstant.HIGHMUSTER,this.frowset.getString("tabid")))
					continue;
				CommonData vo=new CommonData();
				vo.setDataName(this.frowset.getString("cname"));				
				vo.setDataValue(this.frowset.getString("tabid"));
				kq_musterList.add(vo);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return kq_musterList;
	}
	
}
