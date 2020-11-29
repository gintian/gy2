package com.hjsj.hrms.transaction.kq.register.history.statfx;

import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hjsj.hrms.businessobject.kq.register.OrgRegister;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.kq.register.statfx.RegisterStatBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * 历史查询 系统分析 
 * @author Owner
 * wangyao
 */
public class StatAssayTrans extends IBusiness{

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String code=(String)this.getFormHM().get("code");
		String kind=(String)this.getFormHM().get("kind");
		ArrayList courselist=(ArrayList)this.getFormHM().get("courselist");
		String coursedate=(String)this.getFormHM().get("coursedate");//考勤期间
		String registertime = (String) this.getFormHM().get("registertime"); //开始时间
		String jsdatetime = (String) this.getFormHM().get("jsdatetime"); //结束时间
		String file=(String)hm.get("file");
		hm.remove("file");
//		ArrayList kq_dbase_list = (ArrayList)this.getFormHM().get("kq_dbase_list"); //数据库类型
		ArrayList kq_dbase_list = (ArrayList)userView.getPrivDbList();
		if(kq_dbase_list==null||kq_dbase_list.size()<=0)
		{
			kq_dbase_list=userView.getPrivDbList();   //所有数据库类型
		}
		/**得到当前考勤期间 **/
		String cur_course=(String)this.getFormHM().get("start_datetj");
		/*String end_date="";
		try
		{
			ArrayList list=RegisterDate.getKqDayList(this.getFrameconn());
			if(list!=null||list.size()>0)
			{
				
				cur_course=list.get(0).toString();  //当前考勤期间开始时间 1号
				end_date=list.get(1).toString();   // 30号，结束时间
			}
		}catch(Exception e)
		{
			throw new GeneralException("考勤期间未定义,或已经封存！");
//    		throw GeneralExceptionHandler.Handle(e);
		}
		
		this.getFormHM().put("start_datetj", cur_course);
		this.getFormHM().put("end_datetj", end_date);*/
		/**得到封存考核月**/
//		String cur_course="";
//		courselist=registertime.sessionDate(this.frameconn,"1");
//		if(courselist!=null&&courselist.size()>0)
//		{			
//			if(coursedate!=null&&coursedate.length()>0)
//			{
//				cur_course=coursedate;
//			}else{
//				CommonData vo = (CommonData) courselist.get(0);
//				cur_course=vo.getDataValue();
//			}			
//		}
		/*****************/
		if(code==null||code.length()<=0){
			code="";
		}
//		String b0110="";
//		if(code.length()<=0){
//			ManagePrivCode managePrivCode=new ManagePrivCode(userView,this.getFrameconn());
//			b0110=managePrivCode.getUNB0110();  
//		}else{
//			b0110="UN"+code;
//				
//		}
		if(kind==null||kind.length()<=0)
		{
			kind="2";
		}
		/**
		 *页面头展现 
		 */
		ArrayList fielditemlist = DataDictionary.getFieldList("Q03",
				Constant.USED_FIELD_SET);
		String kqname = "KQ_PARAM";
		ArrayList kqq03list = RegisterStatBo.savekqq03list(kqname, this.getFrameconn(),fielditemlist);
		
		 String codesetid="UN";
		 if(!userView.isSuper_admin()) 
         {
		 if("UM".equals(RegisterInitInfoData.getKqPrivCode(userView)))
			codesetid="UM";	
         }
		 kqq03list=RegisterStatBo.newFieldItemListQ09(kqq03list,codesetid);
		 ArrayList a0100whereIN= new ArrayList();
		 for(int i=0;i<kq_dbase_list.size();i++)
		 {
				String dbase=kq_dbase_list.get(i).toString();
				String whereA0100In=RegisterInitInfoData.getWhereINSql(this.userView,dbase);
				a0100whereIN.add(whereA0100In);
		 }
//		 if(codesetid)
//		 {
//			 
//		 }else if()
//		 {
//			 
//		 }
		String whereE0122=OrgRegister.selcet_kq_AllOrgId("e0122",a0100whereIN,"");
		ArrayList orgide0122List=OrgRegister.getQrgE0122List(this.frameconn,whereE0122,"e0122");
		/**得到封存的考核时间 **/
//		ArrayList datelist=HistoryBrowse.registertime(b0110,this.getFrameconn(),this.userView,cur_course,"1");//已经封存的考核时间
//		String cur_date="";
//		String start_date="";
//		String end_date="";
//		if(datelist!=null&&datelist.size()>0)
//		{
//				CommonData vo = (CommonData) datelist.get(0);	
//				cur_date=vo.getDataValue();	
//				start_date=vo.getDataValue();
//				vo = (CommonData) datelist.get(datelist.size()-1);
//				end_date=vo.getDataValue();
//		}
		/*********/
		StringBuffer b0110Str=new StringBuffer();		
		for(int i=0;i<orgide0122List.size();i++)
		{
			b0110Str.append("'"+orgide0122List.get(i).toString()+"',");
		}
		ManagePrivCode managePrivCode=new ManagePrivCode(userView,this.getFrameconn());
		String userOrgId=managePrivCode.getPrivOrgId();  
		if(userOrgId!=null&&userOrgId.length()>0)
		{
			b0110Str.append("'"+userOrgId+"',");
		}
		String b0100s="";
		 if(b0110Str.toString()!=null&&b0110Str.toString().length()>0)
		 {
			 b0100s= b0110Str.toString().substring(0,b0110Str.length()-1);	
		 }else
		 {
			  String error_message=ResourceFactory.getProperty("kq.date.no.record");	
//	 		  this.getFormHM().put("error_message",error_message);
//	 	      this.getFormHM().put("error_return",this.error_return2);  
	 	      this.getFormHM().put("error_flag","2");
	 	      this.getFormHM().put("error_stuts","1");
	 	      return;	
			 //throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.date.no.record"),"",""));
		 }
		 String B0110z=selectB0110(code);
		 String cur_d = cur_course.substring(0,4);
		 cur_d = cur_d+"-PT";
		ArrayList sqllist=RegisterStatBo.getSqlstrHistory(kqq03list,b0100s,cur_d, "Q09",userOrgId,code,B0110z,this.getFrameconn(),file);
		
//		String workcalendar=RegisterInitInfoData.getStatSelectHtml(datelist,cur_date);   //时间下拉框 开始时间
//		this.getFormHM().put("workstat",workcalendar);
//		String workjs=RegisterInitInfoData.getStatSelectjsHtml(datelist,cur_date);   //时间下拉框 结束时间
//		this.getFormHM().put("workjs",workjs);
		this.getFormHM().put("kqq03list", kqq03list);
		this.getFormHM().put("kq_dbase_list",kq_dbase_list);
		if(code!=null||!"".equals(code))
		{
			code="";
		}
		this.getFormHM().put("code",code);
		this.getFormHM().put("kind",kind);
		this.getFormHM().put("registertime", registertime);
		this.getFormHM().put("jsdatetime", jsdatetime);
		this.getFormHM().put("sqlstr", sqllist.get(0).toString());
  		this.getFormHM().put("strwhere", sqllist.get(1).toString());
  		this.getFormHM().put("orderby", sqllist.get(2).toString());
		this.getFormHM().put("columns", sqllist.get(3).toString());
		
	}
	/**
	 * 通过 organization  得到对应的B0110
	 * @param code
	 * @return
	 */
	public String selectB0110(String code)
	{
		String zh="";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		StringBuffer sql1 = new StringBuffer();
		RowSet rowSet=null;
		try
		{

				sql1.append("select codeitemid from organization where grade='1' order by codeitemid");
				rowSet = dao.search(sql1.toString());
				while(rowSet.next())
				{
					zh = rowSet.getString("codeitemid");
				}

		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			if(rowSet!=null){
				try {
					rowSet.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return zh;
	}

}