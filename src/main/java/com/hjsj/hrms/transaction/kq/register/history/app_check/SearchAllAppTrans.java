/*
 * Created on 2006-2-8
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.kq.register.history.app_check;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.SelectAllOperate;
import com.hjsj.hrms.businessobject.kq.app_check_in.SearchAllApp;
import com.hjsj.hrms.businessobject.kq.query.CodingAnalytical;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author liwc
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class SearchAllAppTrans extends IBusiness {

	/**
	 * 校验日期是否正确
	 * @return
	 */
	private boolean validateDate(String datestr)
	{
		boolean bflag=true;
		if(datestr==null|| "".equals(datestr))
			return false;
		try
		{
			java.util.Date date=DateStyle.parseDate(datestr);
			if(date==null)
				bflag=false;
		}
		catch(Exception ex)
		{
			bflag=false;
		}
		return bflag;
	}

	public void execute() throws GeneralException {
		String dotflag = (String)this.getFormHM().get("dotflag");
		if(!"0".equalsIgnoreCase(dotflag))
		{
			this.getFormHM().put("dotflag", "0");
		}
		String query_type=(String)this.getFormHM().get("query_type");
		ArrayList kq_dbase_list = (ArrayList)this.getFormHM().get("kq_dbase_list");	
		if(query_type==null|| "".equals(query_type))
			query_type="1";
		String sp_flag=(String)this.getFormHM().get("sp_flag");
		String full = (String)this.getFormHM().get("full");
		if(sp_flag==null|| "".equals(sp_flag))
			sp_flag="all";
		/**考勤项目（年假、病假、节日加班等类型）*/
		String kq_item=(String)this.getFormHM().get("showtype");
		if(kq_item==null|| "".equals(kq_item))
			kq_item="all";
		this.getFormHM().put("sp_flag",sp_flag);
		this.getFormHM().put("showtype",kq_item);
		String start_date=(String)this.getFormHM().get("start_date");
		String end_date=(String)this.getFormHM().get("end_date");
		if(start_date == null || "".equals(start_date) || end_date == null || "".equals(end_date)){
			ArrayList list = RegisterDate.getKqSealMaxDayList(this.frameconn);
			if(list!=null && list.size()>0){
				start_date = (String)list.get(0);
				end_date = (String)list.get(1);
			}else 
			{
				throw new GeneralException(ResourceFactory.getProperty("kq.register.session.nosave"));
			}
		}
			
			
		if(start_date!=null&&start_date.length()>0)
			start_date=start_date.replaceAll("\\.","-");
		if(end_date!=null&&end_date.length()>0)
			end_date=end_date.replaceAll("\\.","-");
		
		String select_flag=(String)this.getFormHM().get("select_flag");
		
		String select_name=(String)this.getFormHM().get("select_name");
		if("1".equalsIgnoreCase(full)){
			select_name="";
			full="0";
		}
		String select_pre=(String)this.getFormHM().get("select_pre");		
		String select_time_type=(String)this.getFormHM().get("select_time_type");
		  if ("0".equalsIgnoreCase(dotflag)) {
              select_time_type = "0";
              //szk 20131106获取该年最后一天
              end_date = start_date.substring(0, 4) + "-12-31";
          }
		this.getFormHM().put("select_time_type", select_time_type);
	    this.getFormHM().put("select_flag",select_flag);
		this.getFormHM().put("select_name",select_name);
		this.getFormHM().put("full",full);
	      
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String code=(String)this.getFormHM().get("code");
		//this.getFormHM().put("sub_page", hm.get("sub_page"));
		/**
		 *=0 职位
		 *=1 部门
		 *=2 单位
		 */
		String kind=(String)this.getFormHM().get("kind");		
		KqUtilsClass kqUtilsClass=new KqUtilsClass(this.getFrameconn(),this.userView);
		kq_dbase_list=kqUtilsClass.setKqPerList(code,kind);	
		/**申请登记表名*/		
		String table = (String) hm.get("table");
		hm.remove("table");
		DbWizard dbWizard =new DbWizard(this.getFrameconn());
		if(!dbWizard.isExistTable("Q11_arc", false)&&!dbWizard.isExistTable("Q13_arc", false)&&!dbWizard.isExistTable("Q15_arc", false)){
			throw new GeneralException("无归档数据！");
		}
		if(table!=null&&table.length()>3&&!dbWizard.isExistTable(table, false)){
			if("Q11_arc".equalsIgnoreCase(table))
				throw new GeneralException("加班申请未归档！");
			if("Q13_arc".equalsIgnoreCase(table))
				throw new GeneralException("公出申请未归档！");
			if("Q15_arc".equalsIgnoreCase(table))
				throw new GeneralException("请假申请未归档！");
		}
		if(table==null||table.length()<4)
			table=(String)this.getFormHM().get("table");
		table=table==null||table.length()<4?"Q11_arc":table;
		String ta=table.toLowerCase().substring(0,3);
		SearchAllApp searchAllApp=new SearchAllApp(this.getFrameconn(),this.userView);
		/*添加字段*/
		SelectAllOperate selectAllOperate=new SelectAllOperate(this.getFrameconn(),this.userView);
		String frist = (String) hm.get("wo");
		String relatTableid=ta.substring(1);
		ArrayList fieldlist = DataDictionary.getFieldList(ta,Constant.USED_FIELD_SET);// 字段名
		ArrayList searchfieldlist=new ArrayList();
		for(int i=0;i<fieldlist.size();i++)
		{
			FieldItem field_new=new FieldItem();
			FieldItem field=(FieldItem)fieldlist.get(i);
			field.setValue("");
			field.setViewvalue("");
			if("1".equals(field.getState()))
				field.setVisible(true);
			else
				field.setVisible(false);
			if(field.getItemid().equals(ta+"07"))
				this.getFormHM().put("visi", ta+"07");
			//用来判断批量签批是M型还是A代码型
			if(field.getItemid().equals(ta+"11"))
			{
				String bflag=codesetidQ(ta, field.getItemid());
				this.getFormHM().put("bflag", bflag);
			}
			
			field_new=(FieldItem)field.cloneItem();
			searchfieldlist.add(field_new);
		}
		this.getFormHM().put("table", table);
		this.getFormHM().put("searchfieldlist", searchfieldlist);
		  
		
		KqParameter para = new KqParameter(this.userView, "", this.getFrameconn());
           HashMap hashmap = para.getKqParamterMap();
           String cardno = (String) hashmap.get("cardno");
		  StringBuffer cond_str = new StringBuffer();
          String columns = "", columns2 = "", sel_columns = "";
          //	    sql_str.append("select ");
          for (int i = 0; i < fieldlist.size(); i++) {
              FieldItem field = (FieldItem) fieldlist.get(i);
              String itemid = field.getItemid();
              if (columns.length() < 1) {
                  if ("A0100".equalsIgnoreCase(itemid) || "nbase".equalsIgnoreCase(itemid)) {
                      columns = itemid;
                      sel_columns = "Q." + itemid;
                  } else if ( itemid.equalsIgnoreCase(cardno)) {
                      columns = itemid;
                      sel_columns = "A." + itemid;
                  } else {
                      columns = itemid;
                      sel_columns = itemid;
                  }
              } else {
                  if ("A0100".equalsIgnoreCase(itemid) || "nbase".equalsIgnoreCase(itemid)) {
                      columns += "," + itemid;
                      sel_columns += ",Q." + itemid;
                  } else if ( itemid.equalsIgnoreCase(cardno)) {
                      columns += "," + itemid;
                      sel_columns += ",A." + itemid;
                  } else {
                      columns += "," + itemid;
                      sel_columns += "," + itemid;
                  }
              }
          }

          columns = columns + ",state";
          sel_columns = sel_columns + ",Q.state";

          switch (Sql_switcher.searchDbServer()) {
          case Constant.ORACEL: {
              for (int i = 0; i < fieldlist.size(); i++) {
                  FieldItem field = (FieldItem) fieldlist.get(i);
                  if (columns2.length() < 1) {
                      if ("D".equalsIgnoreCase(field.getItemtype())) {
                          columns2 = "TO_CHAR(" + field.getItemid().toString() + ",'YYYY-MM-DD HH24:MI:SS') "
                                  + field.getItemid().toString();
                      } else {
                          columns2 = field.getItemid().toString();
                      }
                  } else {
                      if ("D".equalsIgnoreCase(field.getItemtype())) {
                          columns2 += ",TO_CHAR(" + field.getItemid().toString() + ",'YYYY-MM-DD HH24:MI:SS') "
                                  + field.getItemid().toString();
                      } else {
                          columns2 += "," + field.getItemid().toString();
                      }
                  }
              }
              columns2 = columns2 + ",state";
              break;
          }
          }
		/**考勤项目*/
		this.getFormHM().put("showtypelist",searchAllApp.getShowType(ta,this.getFrameconn()));
		/**审批状态列表*/
		this.getFormHM().put("splist", searchAllApp.getSplist());	

	    /**条件过滤*/
		StringBuffer whereINStr=new StringBuffer();
		cond_str.append(" from ");
		cond_str.append(table);
		cond_str.append(" where ");
		whereINStr.append(" 1=1 ");//过滤条件
		/**左边树节点代码*/
		if(!(code==null|| "".equalsIgnoreCase(code)))
		{
			if("1".equals(kind))
			{
				whereINStr.append(" and e0122 like '"+code+"%'");
			}else if("0".equals(kind))
			{
				whereINStr.append(" and e01a1 like '"+code+"%'");	
			}else
			{
				whereINStr.append(" and b0110 like '"+code+"%'");	
			}			
		}else
		{
			String privcode=RegisterInitInfoData.getKqPrivCode(userView);
			String codevalue=RegisterInitInfoData.getKqPrivCodeValue(userView);
			if("UM".equalsIgnoreCase(privcode))
				whereINStr.append(" and e0122 like '"+codevalue+"%'");
			else if("@K".equalsIgnoreCase(privcode))
				whereINStr.append(" and e01a1 like '"+codevalue+"%'");
			else if("UN".equalsIgnoreCase(privcode))
				whereINStr.append(" and b0110 like '"+codevalue+"%'");
		}		
		start_date=kqUtilsClass.getSafeCode(start_date);
		end_date=kqUtilsClass.getSafeCode(end_date);
		String where_c="";
		if("2".equals(select_flag)){
			 String selectResult = (String)hm.get("selectResult");
			 where_c = " and " + new CodingAnalytical().analytical(selectResult);
		}else if("1".equals(select_flag)){
			 where_c=kqUtilsClass.getWhere_C(select_flag,"a0101",select_name);
		}
		if(where_c!=null&&where_c.length()>0)
			whereINStr.append(" "+where_c+"");
		this.formHM.put(select_flag, "0");
		String cond0=searchAllApp.getWhere2(ta, start_date, end_date, kq_item, sp_flag, query_type,select_time_type);
		if(cond0.length()>0)
		{
			whereINStr.append(" and ");
			whereINStr.append(cond0);
		}
		
		if("Q15_arc".equalsIgnoreCase(table))
		{
			whereINStr.append(" and "+Sql_switcher.isnull("q1517","0")+"=0");
		}	
		String where_is=whereINStr.toString();
	    ArrayList sql_db_list=new ArrayList();
		if(select_pre!=null&&select_pre.length()>0&&!"all".equals(select_pre))
		{
				sql_db_list.add(select_pre);
		}else
		{
				sql_db_list=kq_dbase_list;
	    }
		cond0=searchAllApp.getPrivWhere(kind,code,sql_db_list,ta + "_arc");
		if(cond0.length()>0)
		{
			whereINStr.append(" and a0100 in (");
			whereINStr.append(cond0);
			whereINStr.append(")");
		}
		if(sql_db_list!=null&&sql_db_list.size()>0)
		{
			whereINStr.append(" and (");
			for(int i=0;i<sql_db_list.size();i++)
			{
				whereINStr.append("nbase='"+sql_db_list.get(i).toString()+"'");
				if(i!=sql_db_list.size()-1)
					whereINStr.append(" or ");
			}
			whereINStr.append(")");
		}
//		if (sql_db_list != null && sql_db_list.size() > 0) {
//			
//			String exper = this.userView.getPrivExpression();
//			for (int i = 0; i < sql_db_list.size(); i++) {
//				String dbname = (String)sql_db_list.get(i);
//				String strWhere = "";
//				if (userView.getKqManageValue() != null
//						&& !userView.getKqManageValue().equals(""))
//					strWhere = userView.getKqPrivSQLExpression("",
//							dbname, fieldlist);
//				else
//					strWhere = userView.getPrivSQLExpression(exper,dbname, false, new ArrayList());
//				if(i > 0){
//					whereINStr.append(" OR (A0100 IN( SELECT A0100 " + strWhere + " ) AND nbase = '" + dbname + "')"); 
//				} else {
//					whereINStr.append(" AND ((A0100 IN( SELECT A0100 " + strWhere + " ) AND nbase = '" + dbname + "')"); 
//				}
//			}
//			whereINStr.append(")");
//		}
	    String sql = "";
        
        String tablejoin = "";
        StringBuffer join = new StringBuffer();
        for (Iterator it = sql_db_list.iterator(); it.hasNext();) {
            String nbase = (String) it.next();
            if (join.length() < 1) {
                join.append("SELECT A0100,'" + nbase + "' nbase,"+ cardno + " FROM " + nbase + "A01");
            } else {
                join.append(" UNION SELECT A0100,'" + nbase + "' nbase,"  + cardno + " FROM " + nbase + "A01");
            }
        }

        tablejoin = "SELECT " + sel_columns + " FROM " + table + " Q INNER JOIN (" + join
                + ") A ON Q.A0100 = A.A0100 AND Q.nbase = A.nbase";
        // 		if(this.userView.isSuper_admin())
        tablejoin = tablejoin + " and " + ta + "z5 <> '01'";
        if (where_c != null && where_c.length() > 0)
            tablejoin = tablejoin + where_c;

        if ("Q15".equalsIgnoreCase(table))
            tablejoin = tablejoin + " and Q.q1501 not in (select q1519 from Q15 where Q1519 IS NOT NULL and Q15Z5 = '01')";

        StringBuffer tablejoin_excle = new StringBuffer();
        if ("Q15".equalsIgnoreCase(table))
            tablejoin_excle = tablejoin_excle.append(" and Q.q1501 not in (select q1519 from Q15 where Q15Z5 = '01')");

        switch (Sql_switcher.searchDbServer()) {
        case Constant.ORACEL: {
            sql = searchAllApp.getSQLUnionWhere(kind, code, "(" + tablejoin + ") B", columns2, where_is, sql_db_list);//海关修改:select_time_type=2时全部查询
          
            break;
        }
        default: {
            sql = searchAllApp.getSQLUnionWhere(kind, code, "(" + tablejoin + ") B", columns, where_is, sql_db_list);
          
            break;
        }
        }

		
		String seal_date=(String)this.getFormHM().get("seal_date");//封存的最后一天
		cond_str.append(whereINStr.toString());
		this.getFormHM().put("kq_dbase_list",kq_dbase_list);
		this.getFormHM().put("kq_list",kqUtilsClass.getKqNbaseList(kq_dbase_list));
		this.getFormHM().put("sql_str",sql);
	    this.getFormHM().put("columns",columns);	
	    this.getFormHM().put("cond_str",""); 	 
	    this.getFormHM().put("orderby","order by "+ta+"Z1"); //按起始时间排序	 
	    this.getFormHM().put("seal_date",seal_date);
	    this.getFormHM().put("returnURL","/kq/app_check_in/all_app_data.do?b_search=link&wo="+frist+"&table="+table);
	    // 涉及SQL注入直接放进userView里
 		this.userView.getHm().put("kq_condition", relatTableid+"`"+whereINStr);
	    this.getFormHM().put("relatTableid",relatTableid);
	    this.getFormHM().put("start_date",start_date);
	    this.getFormHM().put("end_date",end_date);
		/**end.*/
	    //显示部门层数
		Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.getFrameconn());
	    String uplevel = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
	    if(uplevel==null||uplevel.length()==0)
	    		uplevel="0";
	    this.getFormHM().put("uplevel",uplevel);
	}
	

	/* 获得考勤列表的第一个值 */
	private String getFirstOfList(ArrayList list) {
		CommonData vo = (CommonData) list.get(0);
		
		return vo.getDataValue();
	}
	
	private ArrayList getOpenList (String kq_year) throws GeneralException{
		
		StringBuffer sbu=new StringBuffer();
		ArrayList dlist=new ArrayList();
		ContentDAO duration_dao = new ContentDAO(this.getFrameconn());
		sbu.append("select  kq_duration,kq_start,kq_end from kq_duration where ");
		sbu.append(" kq_year='"+kq_year+"'");
		sbu.append(" and kq_duration=(select min(kq_duration)  from kq_duration  where finished='0' and kq_year='"+kq_year+"')");
		try {
			this.frowset = duration_dao.search(sbu.toString());           
			while (this.frowset.next()) {
				CommonData durationvo= new CommonData(this.frowset.getString("kq_duration")+'('+ PubFunc.FormatDate(this.frowset.getDate("kq_start")).replaceAll("-","." )+'-'+PubFunc.FormatDate(this.frowset.getDate("kq_end")).replaceAll("-","." )+')',this.frowset.getString("kq_duration")+'('+ PubFunc.FormatDate(this.frowset.getDate("kq_start")).replaceAll("-","." )+'-'+PubFunc.FormatDate(this.frowset.getDate("kq_end")).replaceAll("-","." )+')');
				dlist.add(durationvo);
			}
		} catch (Exception sqle) {
			sqle.printStackTrace();
			throw GeneralExceptionHandler.Handle(sqle);
		}
		
		return dlist;
		
	}
	/* 获得dblist */
	private void getLoginBaseList() throws GeneralException {
		try {
			/** 权限范围内的人员库列表 */
			ArrayList dblist = this.userView.getPrivDbList();
			DbNameBo dbvo = new DbNameBo(this.getFrameconn());
			dblist = dbvo.getDbNameVoList(dblist);
			ArrayList list = new ArrayList();
			for (int i = 0; i < dblist.size(); i++) {
				CommonData vo = new CommonData();
				RecordVo dbname = (RecordVo) dblist.get(i);
				vo.setDataName(dbname.getString("dbname"));
				vo.setDataValue(dbname.getString("pre"));
				list.add(vo);
			}
			//this.getFormHM().put("dblist", list);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	
	private ArrayList getDbase(String dlist) throws GeneralException
	   {
		   
		   StringBuffer stb=new StringBuffer();
		   ContentDAO dao=new ContentDAO(this.getFrameconn());
		   ArrayList dbaselist=userView.getPrivDbList(); 
		   ArrayList slist=new ArrayList();
		  // String[] base=dlist.split(",");
		   try{
			    stb.append("select * from dbname");
			    this.frowset = dao.search(stb.toString());
			    while(this.frowset.next())
			    {
			    	 String dbpre=this.frowset.getString("pre");
	              	   for(int i=0;i<dbaselist.size();i++)
	            	   {
	               		 String userbase=dbaselist.get(i).toString();
	               		  if((dlist.indexOf(userbase)!=-1&&dbpre==userbase)||(dlist.indexOf(userbase)!=-1&&dbpre.equals(userbase)))
	               		  {
	               			  CommonData vo=new CommonData(this.frowset.getString("pre"),this.frowset.getString("dbname"));
	                           slist.add(vo);
	               		  }
	            	   }
			       }
			  } catch(Exception sqle)
	          {
			         sqle.printStackTrace();
			         throw GeneralExceptionHandler.Handle(sqle);            
		       }
			  return slist;
	   }
	
	public String codesetidQ(String teble,String itemid)
	  {
		  String codesetid="";
		  teble = teble.toUpperCase();
		  itemid = itemid.toUpperCase();
		  ContentDAO dao=new ContentDAO(this.getFrameconn());
		  StringBuffer sql = new StringBuffer();
		  RowSet rowSet=null;
		  String itemtype="";
		  String codeid="";
		  try{
			  sql.append("select itemtype,codesetid from t_hr_busifield  where fieldsetid='"+teble+"' and itemid='"+itemid+"'");
			  rowSet=dao.search(sql.toString());
			  while(rowSet.next())
				{
				    itemtype = rowSet.getString("itemtype");
					codeid=rowSet.getString("codesetid");
				}
			  if("A".equals(itemtype)&& "0".equals(codeid))
			  {
				  codesetid="M";
			  }else
			  {
				  codesetid="A";
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
		  return codesetid;
	  }

	
}