package com.hjsj.hrms.transaction.kq.register;


import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.query.CodingAnalytical;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.kq.register.sort.SortBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class DailyRegisterTrans extends IBusiness {
	public void execute() throws GeneralException 
	{		
		String error_flag="0";
		String error_stuts=(String)this.getFormHM().get("error_stuts");
		String error_return=(String)this.getFormHM().get("error_return");
		String sp_flag=(String)this.getFormHM().get("sp_flag");
		String select_flag=(String)this.getFormHM().get("select_flag");
		String select_type = (String)this.getFormHM().get("select_type");	
		String select_name=(String)this.getFormHM().get("select_name");	
		String kqItem = (String) this.getFormHM().get("kqitem");
		kqItem = kqItem == null ? "" : kqItem;
		String kqstatus=(String)this.getFormHM().get("kqstatus");
		kqstatus=kqstatus==null?"all":kqstatus;

		this.getFormHM().put("sp_result","");
		/* -----------显示部门层数-------------------------------------------------- */
		Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.getFrameconn());       //
	    String uplevel = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);         //
	    if(uplevel==null||uplevel.length()==0)                                     //
	    		uplevel="0";                                                       //
	    this.getFormHM().put("uplevel",uplevel);                                   //
	    /* ------------显示部门层数------------------------------------------------- */
	    	
		if(sp_flag==null|| "".equals(sp_flag))
			sp_flag="all";
		if(error_stuts!=null&& "1".equals(error_stuts))
		{
			this.getFormHM().put("error_return",error_return);
			error_flag=(String)this.getFormHM().get("error_flag");
			error_stuts="0";
		}else
		{
			error_stuts="0";
		}
		try{
			//为表添加排序字段
			KqUtilsClass utils = new KqUtilsClass(this.frameconn);
			utils.addColumnToKq("q03");			
			
			HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
			String syncorder = (String) hm.get("syncorder");
			String sortitem = (String) hm.get("sortitem");
			if (sortitem == null ) {
				sortitem = (String) this.getFormHM().get("sortitem");
			} else {
				sortitem = SafeCode.decode(sortitem);
			}
			ArrayList kq_dbase_list=(ArrayList)this.getFormHM().get("kq_dbase_list");
			if (kq_dbase_list.size() == 0 || kq_dbase_list == null) 
			{
	  		   throw new GeneralException(ResourceFactory.getProperty("kq.register.dbase.nosave"));
			}
			String select_pre=(String)this.getFormHM().get("select_pre");		
			ArrayList fieldlist = DataDictionary.getFieldList("Q03",
					Constant.USED_FIELD_SET);
			RegisterInitInfoData registerInitInfoData=new RegisterInitInfoData();
			ArrayList fielditemlist= RegisterInitInfoData.newFieldItemList(fieldlist,this.userView,this.frameconn);
			String where_c="";
	//		RegisterInitInfoData registerInitInfo = new RegisterInitInfoData();
			ArrayList itmelist = registerInitInfoData.getKqItem(this.frameconn);
			DbWizard dbw = new DbWizard(this.frameconn);
	        //** -------------------------郑文龙---------------------- 加 工号、考勤卡号
	        KqParameter para = new KqParameter(this.userView, "", this.getFrameconn());
	        HashMap hashmap = para.getKqParamterMap();
	        String g_no = (String) hashmap.get("g_no");
	        String cardno = (String) hashmap.get("cardno");
	        //** -------------------------郑文龙---------------------- 加 工号、考勤卡号

	        String cur_date="";	
	    	String registerdate = (String) this.getFormHM().get("registerdate");
	    	ArrayList datelist=(ArrayList) this.getFormHM().get("datelist");		
	        CommonData vo = (CommonData) datelist.get(0);			
			// 判断日期是否是从前台传过来的
			if (registerdate != null && registerdate.length() > 0) 
			{
				cur_date = registerdate;
			} else {
				cur_date = vo.getDataValue();// 开始日期
			}
			
			if(dbw.isExistField("q03", "isok", false)){
				String isok=Sql_switcher.isnull("isok", "'正常'");
				ArrayList list = RegisterDate.getKqDayList(frameconn);// 得到未封存最小月的日期list
				String kq_start = (String)list.get(0);
				Date currentDate = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
				String current_date = sdf.format(currentDate);
				if("isok0".equals(kqstatus)){
					
					where_c = " AND ("+isok+" = '正常' OR "+isok+" = '休息')";
				}else if("isok1".equals(kqstatus)){
					if ("all".equalsIgnoreCase(cur_date)) {
						where_c = " AND (("+isok+" <> '正常' AND "+isok+" <> '休息' ";
						where_c = where_c + "AND q03z0 >= '" + kq_start + "' AND q03z0 <= '" + current_date + "'))";
						String kq_type=(String)hashmap.get("kq_type");
						where_c = where_c +  "AND B." + kq_type + " = '02' AND q03z1 > 0 AND modtime is null";
					}else {
						String kq_type=(String)hashmap.get("kq_type");
						where_c = " AND ("+isok+" <> '正常' AND "+isok+" <> '休息' ";
						where_c += "AND B." + kq_type + " = '02' AND q03z1 > 0 AND modtime is null)";
					}
				} 
				ArrayList kqstatuslist=new ArrayList();
				CommonData data = new CommonData();
				data.setDataName("全部");
				data.setDataValue("all");
				kqstatuslist.add(data);
				data=new CommonData();
				data.setDataName("正常");
				data.setDataValue("isok0");
				kqstatuslist.add(data);
				data = new CommonData();
				data.setDataName("异常");
				data.setDataValue("isok1");
				kqstatuslist.add(data);
				this.getFormHM().put("kqstatuslist", kqstatuslist);
			}
			//if(kqstatus.indexOf("isok") != -1){
				//fielditemlist = registerInitInfoData.getNewItemList(fielditemlist, "", this.frameconn);		
			//}else{
				fielditemlist = registerInitInfoData.getNewItemList(fielditemlist, kqItem, this.frameconn);		
			//}
			int lockedNum=RegisterInitInfoData.lockedNum;
			
				if(kq_dbase_list!=null&&kq_dbase_list.size()>0&&kq_dbase_list.size()==1)
					select_pre=kq_dbase_list.get(0).toString();
				else if(select_pre == null )
					select_pre="all";
			
			this.getFormHM().put("select_pre",select_pre);
			String code = (String) hm.get("code");
			String kind = (String) hm.get("kind");  
		    if(kind==null||kind.length()<=0)
		    {
		    	kind=RegisterInitInfoData.getKindValue(kind,this.userView);
		    }
			if(code==null||code.length()<=0){
				code="";
			}
			
			ArrayList sqllist = new ArrayList();
			// 保存部门变动的人员的sql
			ArrayList empChangSQLList = new ArrayList();
			
			if (datelist != null && datelist.size() > 0) 
			{
				ArrayList sql_db_list=new ArrayList();
				if(select_pre!=null&&select_pre.length()>0&&!"all".equals(select_pre))
				{
					sql_db_list.add(select_pre);
				}else
				{
					sql_db_list=kq_dbase_list;
				}
				KqUtilsClass kqUtilsClass=new KqUtilsClass(this.getFrameconn());
	
				
				if("2".equals(select_flag)){
					String whereIN = (String) hm.get("selectResult");
					where_c += " AND" + new CodingAnalytical().analytical(whereIN);
				}else if("1".equals(select_flag)){
					if("0".equals(select_type)){
						where_c += kqUtilsClass.getWhere_C(select_flag,"a0101",select_name);
					}else if("1".equals(select_type)){
						where_c += kqUtilsClass.getWhere_C(select_flag,g_no,select_name);
					}else{
						where_c += kqUtilsClass.getWhere_C(select_flag,cardno,select_name);
					}
				}
	//			String selectResult=(String)this.getFormHM().get("selectResult");
	//			if(selectResult!=null&&selectResult.length()>0)
	//				selectResult=SafeCode.decode(selectResult);
	//			selectResult=PubFunc.keyWord_reback(selectResult);
	//			selectResult=kqUtilsClass.getSelect_WhereResult(select_flag, selectResult);
	//			if(selectResult!=null&&selectResult.length()>0)
	//			{
	//				where_c=where_c+selectResult;
	//				select_flag="1";
	//			}else
	//			{
	//				select_flag="0";
	//			}
	//			this.getFormHM().put("select_flag","0");
				
				fielditemlist = RegisterInitInfoData.isExistsG_noAndCardno("A0101","Q03",g_no,cardno,fielditemlist);
				lockedNum += 2;

				sqllist = RegisterInitInfoData.getSqlstr5(fielditemlist, sql_db_list, cur_date,code,kind," Q03",this.userView,"all",where_c,this.frameconn);
				StringBuffer buf=new StringBuffer();
				StringBuffer buf1 = new StringBuffer();
				if(!"all".equalsIgnoreCase(sp_flag))
				{
					buf.append(" and q03z5");
					buf.append("='");
					buf.append(sp_flag);
					buf.append("'");
				}
	//			String field = RegisterInitInfoData.getKQDEPTParameter();
				String strwhere = "";
	//			if (field.length() > 0) {
	//				empChangSQLList = RegisterInitInfoData.getSqlstr4(fielditemlist, sql_db_list, cur_date,code,kind," Q03",this.userView,"all",where_c);
	//				strwhere=" from  Q03 where" + " ( "+sqllist.get(1).toString().substring((" from "+" Q03"+" where").length())+buf.toString() + ") or ("+  empChangSQLList.get(1)+ buf.toString() + ")";
	//			} else {
				buf1.append(sqllist.get(1));
				if(buf1.lastIndexOf("or")!=-1 && (buf1.lastIndexOf("or")>buf1.lastIndexOf("where")))
				    buf1.insert(buf1.lastIndexOf("or")-1, buf.toString()+" ");
				strwhere=buf1.toString()+buf.toString();
	//			}
				//System.out.println( sqllist.get(0).toString()+" "+strwhere);
				this.getFormHM().put("lockedNum", lockedNum+"");
	      		this.getFormHM().put("sqlstr", sqllist.get(0).toString());
	      		this.getFormHM().put("strwhere", strwhere);
	      		SortBo bo = new SortBo(this.frameconn, this.userView);
	      		String orderby = "";
	      		//2014.10.29 xxd当取得临时排序指标不为空时，进行临时排序，临时排序完成之后清空临时排序指标列
	      		if (sortitem != null && sortitem.length() > 0 && !"not".equalsIgnoreCase(sortitem)) {
	      			orderby = bo.getSortSql(sortitem);
	      			this.getFormHM().put("orderby",bo.getSortSql(sortitem));
	      			hm.remove("sortitem");
	      			this.getFormHM().put("sortitem", "");
	      		} else if(bo.isExistSort()) {
	      			orderby = bo.getSortSql();
	      			this.getFormHM().put("orderby", bo.getSortSql());
	      			sortitem = bo.querrySort();
	      			sortitem = sortitem.replaceAll(",", "`");
	      			this.getFormHM().put("sortitem", "");
	      		} else {
	      			orderby = sqllist.get(2).toString();
	      			this.getFormHM().put("orderby", sqllist.get(2).toString());
	      			this.getFormHM().put("sortitem", "");
	      		}
	            //xiexd 2014.09.12 将导出模板的sql语句保存至服务器
	            String kq_sql = sqllist.get(0).toString()+strwhere+orderby;
	            this.userView.getHm().put("kq_sql_1",kq_sql);
				this.getFormHM().put("columns", sqllist.get(3).toString());	
				// 涉及SQL注入直接放进userView里
				this.userView.getHm().put("kq_condition", "3`"+sqllist.get(4).toString());
				
				String sqlWheres = "";
	//			if (field.length() > 0) {
	//				sqlWheres=" from  Q03 where" + " (( "+sqllist.get(1).toString().substring((" from "+" Q03"+" where").length()) + ") or ("+  empChangSQLList.get(1) + "))";
	//			} else {
					sqlWheres = sqllist.get(1).toString();
	//			}
				String state_message=registerInitInfoData.getStateMessage(sqlWheres,this.getFrameconn());
				this.getFormHM().put("state_message",state_message);
				
			}else{
				//throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.session.nosave"),"",""));
			}		
			/** *****输出数据******* */		
			int num=fielditemlist.size();
			String numstr=""+num;
			this.getFormHM().put("num",numstr);
			this.getFormHM().put("fielditemlist", fielditemlist);
			this.getFormHM().put("datelist", datelist);
			this.getFormHM().put("registerdate",cur_date);
			this.getFormHM().put("kq_dbase_list",kq_dbase_list); 
			this.getFormHM().put("code",code);
			this.getFormHM().put("kind",kind);
			this.getFormHM().put("validate","");		
			CommonData vo_date=(CommonData)datelist.get(0);
			String start_date=vo_date.getDataValue();
			 vo_date=(CommonData)datelist.get(datelist.size()-1);
			String end_date=vo_date.getDataValue();
			/**工作日历**/
			ArrayList showalldatelist = (ArrayList)this.getFormHM().get("showalldatelist");
			String workcalendar=RegisterInitInfoData.getDateSelectHtml(showalldatelist,cur_date);
			this.getFormHM().put("workcalendar",workcalendar);
			this.getFormHM().put("start_date",start_date);
			this.getFormHM().put("end_date",end_date);
			String kq_duration =RegisterDate.getKqDuration(this.getFrameconn());	 
			String pigeonhole_flag=(String)this.getFormHM().get("pigeonhole_flag");
			if(!"true".equals(pigeonhole_flag)&&!"false".equals(pigeonhole_flag)&&!"s_true".equals(pigeonhole_flag)&&!"s_false".equals(pigeonhole_flag))
			{
				this.getFormHM().put("pigeonhole_flag","xxx");
			}
			this.getFormHM().put("kq_duration",kq_duration);
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			registerInitInfoData.cleanState(dao, userView,kq_dbase_list,start_date,end_date,kq_duration);
			
			
			
			if ("1".equals(syncorder)) {
				utils.updateQ03all(start_date, end_date);
			}
	//		if ("2".equals(syncorder)) {
	//			this.getFormHM().put("sortitem", sortitem);
	//		}
			this.getFormHM().put("error_flag",error_flag);
			this.getFormHM().put("error_stuts",error_stuts);
			this.getFormHM().put("showtypelist",RegisterInitInfoData.getShowType());
			this.getFormHM().put("returnURL","/kq/register/daily_registerdata.do?b_query=link");
			this.getFormHM().put("relatTableid","3");		
			this.getFormHM().put("splist", getSplist());
			this.getFormHM().put("kqitemlist",itmelist);
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

		//日明细 展现如果是0不 展现日明细
		String flag="0";
		this.getFormHM().put("flag", flag);
		
		this.getFormHM().put("select_type", select_type);
		this.getFormHM().put("select_name", select_name);
//		
	} 
	/**
	 * 取得审批状态列表
	 */
	public ArrayList getSplist()
	{
		ArrayList list=new ArrayList();
        String sql="select * from codeitem where Codesetid='23' AND codeitemid in ('01','02','03','07','08') order by codeitemid" ;
        try
        {
        	CommonData datavo = new CommonData("all",ResourceFactory.getProperty("label.all"));
    		list.add(datavo);
        	ContentDAO dao=new ContentDAO(this.getFrameconn());
        	this.frowset=dao.search(sql);
        	while(this.frowset.next())
        	{
        		String codevalue=this.frowset.getString("codeitemid");
    			CommonData data=new CommonData(codevalue,this.frowset.getString("codeitemdesc"));
    			list.add(data);
        	}
        }catch(Exception e)
        {
        	
        }		
		return list;
	}	
} 
