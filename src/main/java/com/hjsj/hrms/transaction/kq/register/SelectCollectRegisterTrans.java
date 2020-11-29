package com.hjsj.hrms.transaction.kq.register;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.businessobject.kq.query.CodingAnalytical;
import com.hjsj.hrms.businessobject.kq.register.CollectRegister;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.kq.register.sort.SortBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class SelectCollectRegisterTrans extends IBusiness{
	//u
	   public void execute()throws GeneralException{
		   HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		   HashMap maps=new HashMap();
		   String code = (String) this.getFormHM().get("code");	  
		   ArrayList kq_dbase_list=(ArrayList)this.getFormHM().get("kq_dbase_list");
//		   String select_flag=(String)this.getFormHM().get("select_flag");
		   String select_name=(String)this.getFormHM().get("select_name");
		   String select_pre=(String)this.getFormHM().get("select_pre");
		   String syncorder = (String) hm.get("syncorder");
			String sortitem = (String) hm.get("sortitem");
			if (sortitem == null ) {
				sortitem = (String) this.getFormHM().get("sortitem");
			} else {
				sortitem = SafeCode.decode(sortitem);
			}
			String kqItem = (String) this.getFormHM().get("kqitem");
			kqItem = kqItem == null ? "" : kqItem;
		 //转换小时 1=默认；2=HH:MM
			String selectys=(String)hm.get("selectys");
			if(selectys==null|| "".equals(selectys))
			{
				selectys="1";
			}

			this.getFormHM().put("select_name",select_name);
		   if(kq_dbase_list==null||kq_dbase_list.size()<=0)
		   {
			   kq_dbase_list=userView.getPrivDbList(); 	 			   
		   }
		   String showtype = (String) this.getFormHM().get("showtype");
		   if(showtype==null||showtype.length()<=0)
			{
				showtype="all";
			}	
		   code=code.trim();
		   if(code==null||code.length()<=0)
			{
				 code="";
			}
		   
		   
		   KqUtilsClass utils = new KqUtilsClass(this.frameconn);
		   utils.addColumnToKq("q05");
		   
		   String kq_duration =RegisterDate.getKqDuration(this.getFrameconn());	   
		   this.getFormHM().put("validate","");
		   ArrayList datelist=(ArrayList) this.getFormHM().get("datelist");
		   String kind = (String)this.getFormHM().get("kind");   
		   CommonData vo_date=(CommonData)datelist.get(0);
		   String start_date=vo_date.getDataValue();
		   vo_date=(CommonData)datelist.get(datelist.size()-1);
		   String end_date=vo_date.getDataValue();
		   ArrayList fielditemlist = DataDictionary.getFieldList("Q03",Constant.USED_FIELD_SET);		   
		   ArrayList fieldlist= RegisterInitInfoData.newFieldItemList(fielditemlist,this.userView,this.frameconn);
		   RegisterInitInfoData registerInitInfoData=new RegisterInitInfoData();
		   if(kqItem.indexOf("isok") != -1){
			   fieldlist = registerInitInfoData.getNewItemList(fieldlist, "", this.frameconn);		
		   }else{
			   fieldlist = registerInitInfoData.getNewItemList(fieldlist, kqItem, this.frameconn);		
		   }	
		   int lockedNum=RegisterInitInfoData.lockedNum;
		   KqParam kqParam = KqParam.getInstance();
		   String self_accept_month_data = kqParam.getSelfAcceptMonthData();
		   if("1".equalsIgnoreCase(self_accept_month_data)){
			   FieldItem isconfirm = new FieldItem();
			   DbWizard dWizard = new DbWizard(frameconn);
			   if (dWizard.isExistField("Q05", "accepted", false)) {
				   isconfirm.setFieldsetid("Q05");
				   isconfirm.setItemid("accepted");
				   isconfirm.setItemtype("A");
				   isconfirm.setItemdesc("已确认");
				   isconfirm.setCodesetid("45");
				   fieldlist.add(isconfirm);
			   }
		   }
		   
		   FieldItem fielditem=new FieldItem();
		   fielditem.setFieldsetid("Q05");
		   fielditem.setItemdesc(ResourceFactory.getProperty("kq.register.period"));
		   fielditem.setItemid("scope");
		   fielditem.setItemtype("A");
		   fielditem.setCodesetid("0");
		   fielditem.setVisible(false);
		   fieldlist.add(fielditem); 
		   FieldItem fielditem1=new FieldItem();
		   fielditem1.setFieldsetid("Q05");
		   fielditem1.setItemdesc(ResourceFactory.getProperty("kq.register.overrule"));
		   fielditem1.setItemid("overrule");
		   fielditem1.setItemtype("A");
		   fielditem1.setCodesetid("0");
		   fielditem1.setVisible(true);
		   fieldlist.add(fielditem1); 
		   
		   fieldlist = registerInitInfoData.getNewItemList(fieldlist);//去掉日期指标
		   
		   ArrayList sql_db_list=new ArrayList();
		   if(select_pre!=null&&select_pre.length()>0&&!"all".equals(select_pre))
		   {
				sql_db_list.add(select_pre);
		   }else
		   {
				sql_db_list=kq_dbase_list;
		   }
		   KqUtilsClass kqUtilsClass=new KqUtilsClass(this.getFrameconn());
		   KqParameter para = new KqParameter(userView, "", this.frameconn);
		   HashMap hashmap = para.getKqParamterMap();
		   String g_no = (String) hashmap.get("g_no");
		   String cardno = (String) hashmap.get("cardno");
		   fieldlist = RegisterInitInfoData.isExistsG_noAndCardno("A0101", "Q03", g_no, cardno, fieldlist);
		   lockedNum += 2;
		   String select_type = (String)this.getFormHM().get("select_type");
		   String select_flag = (String)hm.get("select_flag");
		   String where_c= "";
		   if("2".equals(select_flag)){
			    String whereIN = (String) hm.get("selectResult");
			    whereIN=PubFunc.keyWord_reback(whereIN);
			    where_c = " AND" + new CodingAnalytical().analytical(whereIN);
				hm.remove("select_flag");
		   }else if("1".equals(select_flag)){
			   if("0".equals(select_type)){
				   where_c=kqUtilsClass.getWhere_C(select_flag,"a0101",select_name);
			   }else if("1".equals(select_type)){
				   where_c=kqUtilsClass.getWhere_C(select_flag,g_no,select_name);
			   }else{
				   where_c=kqUtilsClass.getWhere_C(select_flag,cardno,select_name);
			   }
			   hm.remove("select_flag");
		   }
//		   String selectResult=(String)this.getFormHM().get("selectResult");
//		   if(selectResult!=null&&selectResult.length()>0)
//				selectResult=SafeCode.decode(selectResult);
//		   selectResult=PubFunc.keyWord_reback(selectResult);
//			selectResult=kqUtilsClass.getSelect_WhereResult(select_flag, selectResult);
//			if(selectResult!=null&&selectResult.length()>0)
//			{
//				where_c=where_c+selectResult;
//				select_flag="1";
//			}else
//			{
//				select_flag="0";
//			}
		   maps=count_Leave();
		   this.getFormHM().put("lockedNum", lockedNum+"");
		   this.getFormHM().put("kqItem_hash",maps);
//		   this.getFormHM().put("select_flag","0");
//	       ArrayList sqllist = CollectRegister.getSqlstr2(fieldlist,sql_db_list,kq_duration,code,kind,"Q05",this.userView,showtype,where_c);	 
	       ArrayList sqllist = RegisterInitInfoData.getSqlstr5(fieldlist,
					sql_db_list, kq_duration, code, kind, "Q05", this.userView,
					showtype, where_c,this.frameconn);
	       this.getFormHM().put("showtype",showtype);
	       this.getFormHM().put("sqlstr", sqllist.get(0).toString());
		   this.getFormHM().put("columns", sqllist.get(3).toString());
		   this.getFormHM().put("strwhere", sqllist.get(1).toString());		  
//		   this.getFormHM().put("orderby", sqllist.get(2).toString());
		   SortBo bo = new SortBo(this.frameconn, this.userView);
		   String orderby = "";
     		if (sortitem != null && sortitem.length() > 0 && !"not".equalsIgnoreCase(sortitem)) {
     			orderby = bo.getSortSql(sortitem);
     			this.getFormHM().put("orderby",bo.getSortSql(sortitem));
     			this.getFormHM().put("sortitem", sortitem);
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
     		//xiexd 2014.09.11 将导出模板的sql语句保存至服务器
            String kq_sql = sqllist.get(0).toString() + sqllist.get(1).toString() + orderby;
            this.userView.getHm().put("kq_sql_1",kq_sql);
		   this.getFormHM().put("fielditemlist", fieldlist);
		   // 涉及SQL注入直接放进userView里
		   this.userView.getHm().put("kq_condition", "5`"+sqllist.get(4).toString());
//		   this.getFormHM().put("condition","5`"+sqllist.get(4).toString());
		   this.getFormHM().put("collectdate", CollectRegister.getMonthRegisterDate(start_date,end_date));
		   this.getFormHM().put("relatTableid","5");
		   this.getFormHM().put("code",code);
		   this.getFormHM().put("datelist",datelist);	
		   this.getFormHM().put("returnURL","/kq/register/select_collectdata.do?b_search=link");
		  
		   String state_message=registerInitInfoData.getStateMessage(sqllist.get(1).toString(),this.getFrameconn());
		   this.getFormHM().put("state_message",state_message);	
			this.getFormHM().put("kqitemlist", registerInitInfoData.getKqItem(this.frameconn));
		   ContentDAO dao=new ContentDAO(this.getFrameconn());
		   registerInitInfoData.cleanState(dao, userView,kq_dbase_list,start_date,end_date,kq_duration);
		   this.getFormHM().put("collectMap", getCollect(sqllist.get(0).toString() + sqllist.get(1).toString(), fieldlist));
		    //月汇总 展现如果是1 不展现月汇总
			String flag="1";
			this.getFormHM().put("flag", flag);
			
			if ("1".equals(syncorder)) {
				//utils.updateQ05(start_date, " where q03z0='"+kq_duration+"'");
				utils.updateQ05all(kq_duration);
			}
	   }

	   private HashMap getCollect(String sql, ArrayList fieldlist) {
		   HashMap map = new HashMap();
		   ContentDAO dao = new ContentDAO(this.frameconn);
		   StringBuffer sqls = new StringBuffer();
		   try {
			  sqls.append("select ");
			  StringBuffer col = new StringBuffer();
			  for (int i = 0; i < fieldlist.size(); i++) {
				  FieldItem fielditem = (FieldItem) fieldlist.get(i); 
				  if ("N".equalsIgnoreCase(fielditem.getItemtype())) {
					  col.append(",");
					  col.append("sum(");
					  col.append(fielditem.getItemid());
					  col.append(") ");
					  col.append(fielditem.getItemid());
					  
				  } else {
					  map.put(fielditem.getItemid().toLowerCase(), "&nbsp;");
				  }
			  }
			  
			  if (col.length() > 0) {
				  sqls.append(col.substring(1));
			  }
			  
			  sqls.append(" from (");
			  sqls.append(sql);
			  sqls.append(") bvbv");
			  FieldItem item;
			  this.frowset = dao.search(sqls.toString());
			  if (this.frowset.next()) {
				 ResultSetMetaData data =  this.frowset.getMetaData();
				 for (int i = 1; i <= data.getColumnCount(); i++) {
					 String column = data.getColumnName(i);
					 item = new FieldItem();
					 item = DataDictionary.getFieldItem(column);
					 int decimal = item.getDecimalwidth();
					 String value = this.frowset.getString(column);
					 value = value == null ? "" : value;
					 map.put(column.toLowerCase(), PubFunc.round(value, decimal));
					 
				 }
				 
			  }
		   } catch (Exception e) {
			   e.printStackTrace();
		   }
		   
		   
		   return map;
	   }
	   
	   /**
		  * 考勤规则的一个hashmap集
		  * @return
		  * @throws GeneralException
		  */
		 private HashMap count_Leave() throws GeneralException
		 {
		    	RowSet rs=null;	    	
		    	String kq_item_sql="select item_id,has_rest,has_feast,item_unit,fielditemid,sdata_src from kq_item";    	    	
		    	
		    	ContentDAO dao=new ContentDAO(this.getFrameconn());
		    	
		    	HashMap hashM=new HashMap();
		    	String fielditemid="";
		    	try
		    	{
		    	   rs =dao.search(kq_item_sql);
		    	   while(rs.next())
		    	   { 
		    		   HashMap hashm_one=new HashMap();	    		  
		    		   if(rs.getString("fielditemid")==null||rs.getString("fielditemid").length()<=0)
		    			   continue;
		    		   ArrayList fielditemlist = DataDictionary.getFieldList("Q03",Constant.USED_FIELD_SET);    
		    		   for(int i=0;i<fielditemlist.size();i++)
		   	    	   {
		   	   	          FieldItem fielditem=(FieldItem)fielditemlist.get(i);
		   	   	          fielditemid=rs.getString("fielditemid");	   	   	          
		   	   	          if(fielditemid.equalsIgnoreCase(fielditem.getItemid()))
		   	   	          {
		   	   	            hashm_one.put("fielditemid",rs.getString("fielditemid"));
			    		    hashm_one.put("has_rest",PubFunc.DotstrNull(rs.getString("has_rest")));
			    		    hashm_one.put("has_feast",PubFunc.DotstrNull(rs.getString("has_feast")));
			    		    hashm_one.put("item_unit",PubFunc.DotstrNull(rs.getString("item_unit")));
			    		    hashm_one.put("sdata_src",PubFunc.DotstrNull(rs.getString("sdata_src")));
			    		    hashM.put(fielditemid,hashm_one);
			    		    continue;
		   	   	          }
		   	    	   }
		    		   
		    	   }
		    	}catch(Exception e)
		    	{
		    		e.printStackTrace();
		    		throw GeneralExceptionHandler.Handle(e);
		    	}finally{
					if(rs!=null){
						try {
							rs.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				}
		    	return hashM;	    	
		 }
}
