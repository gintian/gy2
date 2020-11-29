package com.hjsj.hrms.transaction.train.signCollect;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.train.TransDataBo;
import com.hjsj.hrms.businessobject.train.attendance.TrainAtteBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
/**
 * 汇总培训考勤签到
 * <p>Title:CollectTrainSignInfoTrans.java</p>
 * <p>Description>:CollectTrainSignInfoTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Mar 14, 2011 3:54:54 PM</p>
 * <p>@version: 5.0</p>
 * <p>@author: s.xin
 */
public class CollectTrainSignInfoTrans extends IBusiness {

	public void execute() throws GeneralException {
		String courseplan=(String)this.getFormHM().get("courseplan");
		String classplan=(String)this.getFormHM().get("classplan");
		String sort=(String)this.getFormHM().get("sort");
		
		if(courseplan != null && courseplan.length() > 0)
            courseplan = PubFunc.decrypt(SafeCode.decode(courseplan));
        
        if(classplan != null && classplan.length() > 0)
            classplan = PubFunc.decrypt(SafeCode.decode(classplan));
		
		if(courseplan==null||courseplan.length()<=0)
			throw GeneralExceptionHandler.Handle(new GeneralException(ResourceFactory.getProperty("train.no.take.courseplan.info")));
		if(classplan==null||classplan.length()<=0)
			throw GeneralExceptionHandler.Handle(new GeneralException(ResourceFactory.getProperty("train.no.take.classplan.info")));
		ArrayList fielditemlist=DataDictionary.getFieldList("R47",Constant.USED_FIELD_SET);	
		if("1".equals(sort))//人员
		{
			empCollectSign(classplan,courseplan);			
			ArrayList list=new ArrayList();
		    for(int i=0;i<fielditemlist.size();i++){
			    FieldItem fielditem=(FieldItem)fielditemlist.get(i);			
				if("1".equals(fielditem.getState()))
				{						
					fielditem.setVisible(true);					
				}else
				{
					fielditem.setVisible(false);
				}
				list.add(fielditem.clone());			
		    }
		    this.getFormHM().put("fielditemlist", list);   
			StringBuffer cloums=new StringBuffer();//得到属性列
			for(int i=0;i<fielditemlist.size();i++){
				FieldItem fielditem=(FieldItem)fielditemlist.get(i);
				cloums.append(fielditem.getItemid()+",");					
			}
			cloums.setLength(cloums.length()-1);
			String sql="select "+cloums.toString();
			String where ="from r47 where r4101='"+courseplan+"'";
			String order="order by b0110,e0122";
			this.userView.getHm().put("train_sql", sql + " " + where + " " + order);
            this.userView.getHm().put("train_columns", cloums.toString());
			this.getFormHM().put("sql_str", sql);
			this.getFormHM().put("where_str", where);
			this.getFormHM().put("order_str", order);
			this.getFormHM().put("columns", cloums.toString());
			this.getFormHM().put("loadclass", "false");			
		}else if("2".equals(sort))//课程
		{
			TrainAtteBo trainAtteBo=new TrainAtteBo();
			LazyDynaBean bean=trainAtteBo.getCourseSignCollectSQLParam(classplan,fielditemlist,"");
			this.userView.getHm().put("train_sql", bean.get("sql_str") + " " + bean.get("where_str") + " " + bean.get("order_str"));
            this.userView.getHm().put("train_columns", bean.get("columns"));
			this.getFormHM().put("sql_str", bean.get("sql_str"));
			this.getFormHM().put("where_str",  bean.get("where_str"));
			this.getFormHM().put("order_str", bean.get("order_str"));
			this.getFormHM().put("columns",bean.get("columns"));			
			this.getFormHM().put("fielditemlist", bean.get("fielditemlist")); 
			this.getFormHM().put("loadclass", "false");
			
		}else if("3".equals(sort))//班组
		{
			String timeflag = (String)this.getFormHM().get("timeflag");
			timeflag=timeflag!=null&&timeflag.trim().length()>0?timeflag:"";
			timeflag= "00".equalsIgnoreCase(timeflag)?"":timeflag;
			
			String startime = (String)this.getFormHM().get("startime");
			startime=startime!=null&&startime.trim().length()>0?startime:"";
			
			String endtime = (String)this.getFormHM().get("endtime");
			endtime=endtime!=null&&endtime.trim().length()>0?endtime:"";
			TrainAtteBo trainAtteBo=new TrainAtteBo();
			TransDataBo transbo = new TransDataBo(this.getFrameconn(),"3"); 
			String times = transbo.timesSql(timeflag,startime,endtime);
			String a_code=(String)this.getFormHM().get("a_code");
			LazyDynaBean bean=trainAtteBo.getClassSignCollectSQLParam(a_code,times,"","04,06",fielditemlist);
			
            this.userView.getHm().put("train_sql", bean.get("sql_str") + " " + bean.get("where_str") + " " + bean.get("order_str"));
            this.userView.getHm().put("train_columns", bean.get("columns"));
			this.getFormHM().put("sql_str", bean.get("sql_str"));
			this.getFormHM().put("where_str",  bean.get("where_str"));
			this.getFormHM().put("order_str", bean.get("order_str"));
			this.getFormHM().put("columns",bean.get("columns"));			
			this.getFormHM().put("fielditemlist", bean.get("fielditemlist")); 
		}
		
	}
	/**
	 * 统计人员
	 * @param classplan 培训班编号
	 * @param courseplan 培训人员编号
	 */
	private void empCollectSign(String classplan,String courseplan)throws GeneralException 
	{
		StringBuffer sql=new StringBuffer();		
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try {
			
			ArrayList values=new ArrayList();
			//得到课程的总时间
			//select * from tr_classplan where r4101='0000000030'
			sql.setLength(0);
			sql.append("select id,train_date,begin_time,end_time,class_len,begin_card,end_card from tr_classplan where r4101=? order by train_date,begin_time");
			values.clear();
			values.add(courseplan);
			//System.out.println("###############"+sql.toString());
			this.frowset=dao.search(sql.toString(), values);
			float class_len=0f;//课程的总时间
			int class_num=0;//几节课
			ArrayList classlist=new ArrayList();			
			String date_str="";
			String begin_time="";
			String end_time="";		
			int allow_B=0;//允许开始签到
			int allow_E=0;
			while(this.frowset.next())
			{
				class_len=class_len+this.frowset.getFloat("class_len");
				Date date=this.frowset.getDate("train_date");
				date_str=DateUtils.format(date,"yyyy.MM.dd");
				begin_time=this.frowset.getString("begin_time");
				end_time=this.frowset.getString("end_time");
				int begin_card=this.frowset.getInt("begin_card");
				allow_B=allow_B+begin_card;				
				int end_card=this.frowset.getInt("end_card");
				allow_E=allow_E+end_card;
				LazyDynaBean bean=new LazyDynaBean();
				Date b_T=DateUtils.getDate(date_str+" "+begin_time, "yyyy.MM.dd HH:mm");
				Date e_T=DateUtils.getDate(date_str+" "+end_time, "yyyy.MM.dd HH:mm");
				bean.set("id", new Integer(this.frowset.getInt("id")));
				bean.set("b_T", b_T);
				bean.set("e_T", e_T);
				bean.set("b_T_str", date_str+" "+begin_time);
				bean.set("e_T_str", date_str+" "+end_time);
				bean.set("b_C", new Integer(begin_card));
				bean.set("e_C", new Integer(end_card));
				bean.set("class_len", new Float(this.frowset.getFloat("class_len")));
				class_num++;
				classlist.add(bean);					
			}
	       // System.out.println("分析培训统计数据"+"--"+classplan+"--"+courseplan+"--"+classlist+"--"+class_len+"--"+allow_B+"--"+allow_E+"--"+class_num);
			dataAnalys(classplan,courseplan,classlist,class_len,allow_B,allow_E,class_num);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 分析培训统计数据
	 * @param classplan
	 * @param courseplan
	 * @param classlist
	 * @param class_len
	 * @param allow_B
	 * @param allow_E
	 * @param class_num
	 * @return
	 * @throws GeneralException
	 */
	public boolean dataAnalys(String classplan,String courseplan,ArrayList classlist,float class_len,int allow_B,int allow_E,int class_num)throws GeneralException
	{
		String destTab="t#"+this.userView.getUserName()+"_1";
		KqUtilsClass kqUtilsClass=new KqUtilsClass(this.getFrameconn());
		kqUtilsClass.dropTable(destTab);//删除临时表
		kqUtilsClass.createTempTable("R47", destTab,"*", "1=2", "");//新增临时表
		addColimnsTotempTable(destTab);//向临时表添加字段
		StringBuffer sql=new StringBuffer();
		sql.append("insert into "+destTab+"(nbase,a0100,r4101,r4701,r4711,ISNormal,b_cards,e_cards)");//人员库、人员编号、课程编号、应出勤时间、应出勤次数、正常标记、上课签到次数、下课签退次数
		sql.append("(select nbase,r4001,'"+courseplan+"',"+class_len+","+class_num+",0,0,0 from R40 where r4013='03' and r4005='"+classplan+"' group by nbase,r4001)");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try {
			dao.insert(sql.toString(), new ArrayList());//讲本班人员插入到临时表中
		    sql.setLength(0);
		    //分人员库
		    sql.append("select nbase from "+destTab+" group by nbase");
		    this.frowset=dao.search(sql.toString());
		    ArrayList nbaselist=new ArrayList();
		    while(this.frowset.next())
		    {
		    	nbaselist.add(this.frowset.getString("nbase"));
		    }
		    sql.setLength(0);
		    String nbase="";
		    //建立一个课程刷卡表，统计一个课程刷卡时间段，到底签到、签退刷了多少次卡
		    initCollect(classlist,nbaselist,destTab,classplan,courseplan,dao);
		    //统计迟到的和早退的
		    String update="";
		    for(int i=0;i<nbaselist.size();i++)
	    	{
		    	nbase=(String)nbaselist.get(i);		
		    	String strJoin=destTab+".a0100=A.a0100";
                String strSet="";
                String strDWhere="nbase='"+nbase+"'";
		    	if(allow_B>0)//统计签到记录数
			    {
			    	//1:签到；3：补签到
			    	   		
		    		//将签到次数统计到临时表
		    		StringBuffer srcTab=new StringBuffer();
		    		/*srcTab.append("(select a0100,count(*) cards from tr_cardtime where r4101='"+courseplan+"'");
		    		srcTab.append(" and nbase='"+nbase+"' and (card_type=1 or card_type=3) group by a0100) A");//card_type=1 or card_type=3 1:签到；3：补签到
		    		strSet=destTab+".b_cards=A.cards";
		    		update=Sql_switcher.getUpdateSqlTwoTable(destTab,srcTab.toString(),strJoin,strSet,strDWhere,"");	
					update=KqUtilsClass.repairSqlTwoTable(srcTab.toString(),strJoin,update,strDWhere,"");*/
					//dao.update(update);	
					srcTab.setLength(0);
					//统计迟到late_for次数
					srcTab.append("(select a0100,count(*) lates from tr_cardtime where r4101='"+courseplan+"'");
		    		srcTab.append(" and nbase='"+nbase+"' and (card_type=1 or card_type=3) and late_for>0 group by a0100) A");//card_type=1 or card_type=3 1:签到；3：补签到
		    		strSet=destTab+".R4715=A.lates";
		    		update=Sql_switcher.getUpdateSqlTwoTable(destTab,srcTab.toString(),strJoin,strSet,strDWhere,"");	
					update=KqUtilsClass.repairSqlTwoTable(srcTab.toString(),strJoin,update,strDWhere,"");
                    //System.out.println(update);
					dao.update(update);	
			    }
		    	if(allow_E>0)//统计签退记录数
			    {
			    	//2:签退；4：补签退
			    	//将签退次数统计到临时表
		    		StringBuffer srcTab=new StringBuffer();
		    		/*srcTab.append("(select a0100,count(*) cards from tr_cardtime where r4101='"+courseplan+"'");
		    		srcTab.append(" and nbase='"+nbase+"' and (card_type=2 or card_type=4) group by a0100) A");//card_type=1 or card_type=3 2:签退；4：补签退
	                strSet=destTab+".e_cards=A.cards";	               
		    		update=Sql_switcher.getUpdateSqlTwoTable(destTab,srcTab.toString(),strJoin,strSet,strDWhere,"");	
					update=KqUtilsClass.repairSqlTwoTable(srcTab.toString(),strJoin,update,strDWhere,"");*/
					//dao.update(update);
					//统计早退leave_early次数
					srcTab.setLength(0);
					srcTab.append("(select a0100,count(*) leave from tr_cardtime where r4101='"+courseplan+"'");
		    		srcTab.append(" and nbase='"+nbase+"' and (card_type=2 or card_type=4) and leave_early>0 group by a0100) A");//card_type=1 or card_type=3 1:签到；3：补签到
		    		strSet=destTab+".R4717=A.leave";
		    		update=Sql_switcher.getUpdateSqlTwoTable(destTab,srcTab.toString(),strJoin,strSet,strDWhere,"");	
					update=KqUtilsClass.repairSqlTwoTable(srcTab.toString(),strJoin,update,strDWhere,"");
					dao.update(update);	
			    }		    	
	    	}
		    sql.setLength(0);
		    //将签到和签退记录相等的人员在临时表ISNormal设置为正常 ：1
		    sql.append("update "+destTab+" set ISNormal=1 where b_cards="+allow_B+" and e_cards="+allow_E+"");
		    dao.update(sql.toString());
		    StringBuffer srcTab=new StringBuffer();
		    String strJoin="";
		    String strSet="";
		    String strDWhere="";
//		    //统计正常人员的迟到时间和早退时间和
//			srcTab.append("(select nbase,a0100,sum(leave_early) leave_early,sum(late_for) late_for");
//		    srcTab.append(" from tr_cardtime where r4101='"+courseplan+"' group by nbase,a0100) A");
//			String strJoin=destTab+".a0100=A.a0100 and "+destTab+".nbase=A.nbase";
//			String strSet=destTab+".r4705=A.late_for`"+destTab+".r4707=A.leave_early";
//			String strDWhere=destTab+".ISNormal=1";
//			update=Sql_switcher.getUpdateSqlTwoTable(destTab,srcTab.toString(),strJoin,strSet,strDWhere,"");	
//		    update=KqUtilsClass.repairSqlTwoTable(srcTab.toString(),strJoin,update,strDWhere,"");
//		    dao.update(update);		    
//		    //统计正常状态的实出勤时间和实出勤次数
//		    sql.setLength(0);
//		    sql.append("update "+destTab+" set R4713="+class_num+",R4703="+Sql_switcher.isnull("R4701","0")+"-("+Sql_switcher.isnull("r4705","0")+"+"+Sql_switcher.isnull("r4707","0")+")/60");//迟到.早退都是分钟
//		    sql.append(" where ISNormal=1");
//		    dao.update(sql.toString());
		    //处理异常状态的人		    
		    sql.setLength(0);
		    sql.append("select nbase,a0100 from "+destTab);//+" where ISNormal<>1");
		    List values_list=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());
		    if(values_list!=null&&values_list.size()>0)
		    {
		    	nbase="";
		    	String a0100="";
		    	for(int i=0;i<values_list.size();i++)
		    	{
		    		LazyDynaBean bean=(LazyDynaBean)values_list.get(i);
		    		nbase=(String)bean.get("nbase");
		    		a0100=(String)bean.get("a0100");
		    		collectUnusualRestul(destTab,a0100,nbase,courseplan,classlist);		    		
		    	}
		    }
		    //向临时表更新人员姓名单位、部门、职位
		    for(int i=0;i<nbaselist.size();i++)
	    	{
		    	nbase=(String)nbaselist.get(i);	
		    	String srcTab2=nbase+"A01";
		    	strJoin=destTab+".a0100="+srcTab2+".a0100";		    	
		    	strSet=destTab+".A0101="+srcTab2+".A0101`"+destTab+".B0110="+srcTab2+".B0110`"+destTab+".E0122="+srcTab2+".E0122`"+destTab+".E01A1="+srcTab2+".E01A1";//更新串  xxx.field_name=yyyy.field_namex,....
		    	update=Sql_switcher.getUpdateSqlTwoTable(destTab,srcTab2,strJoin,strSet,"","nbase='"+nbase+"'");
		    	update=KqUtilsClass.repairSqlTwoTable(srcTab2,strJoin,update,"","nbase='"+nbase+"'");
		    	dao.update(update);
	    	}
		    //删除R47对应课程中的人员
			sql.setLength(0);
			sql.append("delete from R47 where R4101='"+courseplan+"'");
			dao.update(sql.toString());
			//将临时表数据插入到R47
			sql.setLength(0);
			sql.append("insert into r47(nbase,a0100,r4101,a0101,b0110,e0122,e01a1,");
			sql.append("r4701,r4703,r4705,r4707,r4709,r4711,r4713,r4715,r4717,r4719)");
			sql.append("(select nbase,a0100,r4101,a0101,b0110,e0122,e01a1,");
			sql.append("r4701,r4703,r4705,r4707,r4709,r4711,r4713,r4715,r4717,r4719");
			sql.append(" from "+destTab+")");
			dao.insert(sql.toString(), new ArrayList());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}finally{
			kqUtilsClass.dropTable(destTab);//删除临时表
		}
		return true;
	}
	/**
	 * 给临时表增加字段
	 * @param destTab
	 */
	private void addColimnsTotempTable(String destTab)
	{
		  Table table=new Table(destTab);
		  Field temp = new Field("b_cards","签到刷卡次数");
		  temp.setDatatype(DataType.INT);		 
		  table.addField(temp);
		  temp = new Field("e_cards","签退刷卡次数");
		  temp.setDatatype(DataType.INT);		 
		  table.addField(temp);	
		  //首钢增加指标 1为正常 0不正常
		  temp=new Field("ISNormal","正常标记");//缺刷标记
		  temp.setDatatype(DataType.INT);
		  table.addField(temp);
		  DbWizard dbWizard =new DbWizard(this.getFrameconn());
		  try
		  {
			  dbWizard.addColumns(table);
		  }catch(Exception e)
		  {
			  e.printStackTrace();
		  }
		  DBMetaModel dbmodel=new DBMetaModel(this.getFrameconn());
		  dbmodel.reloadTableModel(destTab);	
	}
	/**
	 * 统计有异常的培训签到数据
	 * @param destTab 目标表
	 * @param a0100
	 * @param nbase
	 * @param courseplan 课程id
	 * @param classlist 课程排课明细
	 */
	private void collectUnusualRestul(String destTab,String a0100,String nbase,String courseplan,ArrayList classlist)
	{

		float duty_time=0f;//出勤时间
		float late_time=0f;//迟到时间
		float leave_time=0f;//早退时间
		float lost_duty_time=0f;//缺勤时间
		int dutys=0;//出勤次数
		int lates=0;//迟到次数
		int leaves=0;//早退次数
		int lost_dutys=0;//缺勤次数
		StringBuffer sql=new StringBuffer();
		String over_begin_time="";
		RowSet rs=null;
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		boolean isLost=false;;
		boolean isDutys=false;		
		Date cur_date=new Date();//当前系统时间
		float late_leave=0f;//迟到和早退的课时数
		/*if(a0100.equals("00000032"))
			System.out.println("断点");*/
		try
		{
			for(int i=0;i<classlist.size();i++)
		    {
				float late=0f;
				float leave=0f;//当前课时的迟到和早退情况
		    	LazyDynaBean bean=(LazyDynaBean)classlist.get(i);
		    	Integer begin_card_I=(Integer)bean.get("b_C");//是否允许签到
		    	int begin_card=begin_card_I.intValue();
		    	Integer end_card_I=(Integer)bean.get("e_C");//是否允许签退	   
		    	int end_card=end_card_I.intValue();
		    	Float class_len_F=(Float)bean.get("class_len");
		    	float class_len=class_len_F.floatValue();
		    	String b_T_str=(String)bean.get("b_T_str");
		    	String e_T_str=(String)bean.get("e_T_str");
		    	Date b_T=(Date)bean.get("b_T");
		    	Date e_T=(Date)bean.get("e_T");
		    	isLost=false;
		    	isDutys=false;	
		    	/*leave_time=0;
		    	late_time=0;*/
		    	//判断签到		    	
		    	sql.setLength(0);
		    	duty_time=duty_time+class_len;
		    	if(begin_card==0&&end_card==0)
		    	{
		    		
		    		//如果签到、签退都不需要刷卡，则算全勤
		    		dutys++;
		    		over_begin_time=e_T_str;
		    		continue;
		    	}
		    	if(begin_card==1)		    	
		    	{
		    		if(i==0)
		    		{
		    			//b_T=DateUtils.addDays(b_T, -10);//开始时间向前10天
		    			b_T=DateUtils.getDate(DateUtils.format(b_T, "yyyy.MM.dd")+" 00:00:00", "yyyy.MM.dd HH:mm:ss");
		    		   //第一节课签到
		    			b_T_str=DateUtils.format(b_T, "yyyy.MM.dd HH:mm:ss");//第一节课有很久以前的时间
		    		}else
		    		{
		    			b_T_str=over_begin_time;//以后开始时间用上一个课时的结束时间，结束时间用当前课程的结束时间
		    		}
		    		//找到刷卡时间范围内的签到数据
		    		sql.setLength(0);
		    		///System.out.println("签到");
		    		sql.append("SELECT * FROM tr_cardtime WHERE ");
	    			sql.append(" a0100='"+a0100+"' and nbase='"+nbase+"' and R4101='"+courseplan+"'");
	    			sql.append(" and card_time BETWEEN "+Sql_switcher.dateValue(b_T_str)+" and "+Sql_switcher.dateValue(e_T_str));
	    			sql.append(" and (card_type=1 or card_type=3) order by card_time");
	    			//System.out.println(sql.toString());
	    			rs=dao.search(sql.toString());
		    		if(rs.next())//取第一条
		    		{
		    			dutys++;//出勤一次
		    			isDutys=true;		    			
		    			if(rs.getFloat("late_for")>0)
		    			{
		    				late=rs.getFloat("late_for");
		    				late_time=late_time+rs.getFloat("late_for");//累计迟到时间	
		    				lates++;//迟到次数++
		    			}
		    		}else
		    		{
		    			//没有找到符合时间范围内的数据
		    			lost_dutys++;
		    			lost_duty_time=lost_duty_time+class_len;//当前课程的时间长度
		    			isLost=true;
		    		}
		    	}
		    	if(end_card==1)//&&!isLost)//签退只判断有没有早退即可
		    	{
		    		b_T_str=(String)bean.get("b_T_str");
		    		if(i==(classlist.size()-1))
		    		{
		    			//e_T=DateUtils.addDays(e_T, 10);//结束时间向后10天
		    			e_T=DateUtils.getDate(DateUtils.format(e_T, "yyyy.MM.dd")+" 23:59:59", "yyyy.MM.dd HH:mm:ss");
		    			e_T_str=DateUtils.format(e_T, "yyyy.MM.dd HH:mm:ss");//最后一节课
		    		}else
		    		{
		    			if(classlist.size()>i+1)
		    			{
		    				LazyDynaBean nextbean=(LazyDynaBean)classlist.get(i+1);
		    				e_T_str=(String)nextbean.get("b_T_str");
		    			}
		    		}
		    		//找到刷卡时间范围内的签到数据
		    		sql.setLength(0);
		    		//System.out.println("签退");
		    		sql.append("SELECT * FROM tr_cardtime WHERE ");
	    			sql.append(" a0100='"+a0100+"' and nbase='"+nbase+"' and R4101='"+courseplan+"'");
	    			sql.append(" and card_time BETWEEN "+Sql_switcher.dateValue(b_T_str)+" and "+Sql_switcher.dateValue(e_T_str));
	    			sql.append(" and (card_type=2 or card_type=4) order by card_time desc");
	    			//System.out.println(sql.toString());
	    			rs=dao.search(sql.toString());
		    		if(rs.next())//取最后一条
		    		{
		    			if(rs.getFloat("leave_early")>0)
		    			{
		    				leave=rs.getFloat("leave_early");
							if (isLost) {
								leave = 0;
							}
		    				leave_time=leave_time+rs.getFloat("leave_early");//累计早退时间	
		    				leaves++;//迟到次数++
		    			}
		    			if(begin_card==0)
		    			{
		    				dutys++;//出勤一次		    				
			    			isDutys=true;
		    			}
		    		}else
		    		{
		    			
		    			if(!isLost)//签到是如果没有算缺勤
		    			{
		    				//需要签退不签，就算缺勤，没有找到符合时间范围内的数据
		    				lost_dutys++;
			    			lost_duty_time=lost_duty_time+class_len;//当前课程的时间长度
							late = 0;
		    			}
		    			if(isDutys)
		    				dutys--;
		    		}
		    	}
		    	//over_begin_time=e_T_str;
		    	over_begin_time=DateUtils.format(e_T, "yyyy.MM.dd HH:mm");
		    	
		    	float temp=getMinute(dao,((Integer)bean.get("id")).intValue());
		    	late_leave=late_leave+(late+leave)/temp;//迟到和早退的课时数
		    }
			
//			System.out.println(late_leave);
//			System.out.println((late_time+leave_time)/60);
			lost_duty_time=lost_duty_time+late_leave;//缺勤时间
			if(lost_duty_time>duty_time){
				lost_duty_time=duty_time;
			}
			duty_time=duty_time-lost_duty_time;
			ArrayList list=new ArrayList();
			//System.out.println(duty_time+"--"+late_time+"--"+leave_time+"--"+lost_duty_time+"--"+dutys+"-"+lates+"-"+leaves+"--"+lost_dutys);
			list.add(new Float(duty_time));//实出勤
			list.add(new Float(late_time));//迟到时间
			list.add(new Float(leave_time));//早退时间			
			list.add(new Float(lost_duty_time));//缺勤时间
			list.add(new Integer(dutys));//实出勤次数			
			list.add(new Integer(lates));//迟到次数
			list.add(new Integer(leaves));//早退次数
			list.add(new Integer(lost_dutys));//缺勤次数
			sql.setLength(0);
			sql.append("update "+destTab+" set r4703=?,r4705=?,r4707=?,r4709=?,");
			sql.append("r4713=?,r4715=?,r4717=?,r4719=?");
			sql.append(" where a0100='"+a0100+"' and nbase='"+nbase+"' and R4101='"+courseplan+"'");
			dao.update(sql.toString(), list);
		}catch(Exception e)
		{
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
	}
	/**
	 * 建立一个课程刷卡表，统计一个课程刷卡时间段，到底签到、签退刷了多少次卡
	 * @param classlist
	 * @param nbaselist
	 * @param destTab	 
	 * @param classplan
	 * @param courseplan
	 * @param dao
	 * @throws SQLException
	 */
	private void initCollect(ArrayList classlist,ArrayList nbaselist,String destTab,String classplan,String courseplan,ContentDAO dao) throws SQLException
	{
		float duty_time=0f;//出勤时间
		float late_time=0f;//迟到时间
		float leave_time=0f;//早退时间
		float lost_duty_time=0f;//缺勤时间
		int dutys=0;//出勤次数
		int lates=0;//迟到次数
		int leaves=0;//早退次数
		int lost_dutys=0;//缺勤次数
		StringBuffer sql=new StringBuffer();
		String over_begin_time="";
		RowSet rs=null;		
		boolean isLost=false;;
		boolean isDutys=false;		
		Date cur_date=new Date();//当前系统时间
		String signCardTable="";
		KqUtilsClass kqUtilsClass=new KqUtilsClass(this.getFrameconn());
		try
		{
			signCardTable=signTempCardTable(classlist,classplan,dao,kqUtilsClass);//签到卡片
			//System.out.println(signCardTable);
			String strJoin=signCardTable+".a0100=A.a0100 and "+signCardTable+".nbase=A.nbase";
            String strSet="";
            String update="";
            StringBuffer b_c=new StringBuffer();
            StringBuffer e_c=new StringBuffer();
			for(int i=0;i<classlist.size();i++)
		    {
				LazyDynaBean bean=(LazyDynaBean)classlist.get(i);
		    	Integer begin_card_I=(Integer)bean.get("b_C");//是否允许签到
		    	int begin_card=begin_card_I.intValue();
		    	Integer end_card_I=(Integer)bean.get("e_C");//是否允许签退	   
		    	int end_card=end_card_I.intValue();		    	
		    	String b_T_str=(String)bean.get("b_T_str");
		    	String e_T_str=(String)bean.get("e_T_str");
		    	String b_column="b_c_"+i;
		    	String e_column="e_c_"+i;
		    	Date b_T=(Date)bean.get("b_T");
		    	Date e_T=(Date)bean.get("e_T");
		    	b_c.append(Sql_switcher.isnull(b_column, "0")+"+");
		    	e_c.append(Sql_switcher.isnull(e_column, "0")+"+");
		    	if(begin_card==1)		    	
		    	{
		    		if(i==0)
		    		{
		    			//b_T=DateUtils.addDays(b_T, -10);//开始时间向前10天
		    			b_T=DateUtils.getDate(DateUtils.format(b_T, "yyyy.MM.dd")+" 00:00:00", "yyyy.MM.dd HH:mm:ss");
		    		   //第一节课签到
		    			b_T_str=DateUtils.format(b_T, "yyyy.MM.dd HH:mm:ss");//第一节课有很久以前的时间
		    		}else
		    		{
		    			b_T_str=over_begin_time;//以后开始时间用上一个课时的结束时间，结束时间用当前课程的结束时间
		    		}
		    		//1:签到；3：补签到	    	   		
		    		//将签到次数统计到打卡临临时表
		    		StringBuffer srcTab=new StringBuffer();
		    		srcTab.append("(select nbase,a0100,count(*) cards from tr_cardtime where r4101='"+courseplan+"'");
		    		srcTab.append(" and card_time BETWEEN "+Sql_switcher.dateValue(b_T_str)+" and "+Sql_switcher.dateValue(e_T_str));
		    		srcTab.append("  and (card_type=1 or card_type=3) group by nbase,a0100) A");//card_type=1 or card_type=3 1:签到；3：补签到
		    		strSet=signCardTable+"."+b_column+"=A.cards";
		    		update=Sql_switcher.getUpdateSqlTwoTable(signCardTable,srcTab.toString(),strJoin,strSet,"","");	
					update=KqUtilsClass.repairSqlTwoTable(srcTab.toString(),strJoin,update,"","");		
					//System.out.println(update);
					dao.update(update);	
					srcTab.setLength(0);					
		    	}
		    	if(end_card==1)//签退只判断有没有早退即可
		    	{
		    		
		    		b_T_str=(String)bean.get("b_T_str");
		    		if(i==(classlist.size()-1))
		    		{
		    			//e_T=DateUtils.addDays(e_T, 10);//结束时间向后10天
		    			e_T=DateUtils.getDate(DateUtils.format(e_T, "yyyy.MM.dd")+" 23:59:59", "yyyy.MM.dd HH:mm:ss");
		    			e_T_str=DateUtils.format(e_T, "yyyy.MM.dd HH:mm:ss");//最后一节课
		    		}else
		    		{
		    			if(classlist.size()>i+1)
		    			{
		    				LazyDynaBean nextbean=(LazyDynaBean)classlist.get(i+1);
		    				e_T_str=(String)nextbean.get("b_T_str");
		    			}
		    		}
		    		//2:签退；4：补签退
			    	//将签退次数统计到临时表
		    		StringBuffer srcTab=new StringBuffer();
		    		srcTab.append("(select nbase,a0100,count(*) cards from tr_cardtime where r4101='"+courseplan+"'");
		    		srcTab.append(" and card_time BETWEEN "+Sql_switcher.dateValue(b_T_str)+" and "+Sql_switcher.dateValue(e_T_str));
		    		srcTab.append(" and (card_type=2 or card_type=4) group by nbase,a0100) A");//card_type=1 or card_type=3 2:签退；4：补签退
		    		strSet=signCardTable+"."+e_column+"=A.cards";               
		    		update=Sql_switcher.getUpdateSqlTwoTable(signCardTable,srcTab.toString(),strJoin,strSet,"","");	
					update=KqUtilsClass.repairSqlTwoTable(srcTab.toString(),strJoin,update,"","");
					dao.update(update);
		    	}
		    	//over_begin_time=e_T_str;
		    	over_begin_time=DateUtils.format(e_T, "yyyy.MM.dd HH:mm");		    	
		    }
			if(b_c.length()>0)
			{
				b_c.setLength(b_c.length()-1);
				update="update "+signCardTable+" set b_c="+b_c.toString();				
				dao.update(update);
			}
			if(e_c.length()>0)
			{
				e_c.setLength(e_c.length()-1);
				update="update "+signCardTable+" set e_c="+e_c.toString();
				dao.update(update);
			}
			strSet=destTab+".b_cards="+signCardTable+".b_c`"+destTab+".e_cards="+signCardTable+".e_c";
			strJoin=signCardTable+".a0100="+destTab+".a0100 and "+signCardTable+".nbase="+destTab+".nbase";
			update=Sql_switcher.getUpdateSqlTwoTable(destTab,signCardTable,strJoin,strSet,"","");	
			update=KqUtilsClass.repairSqlTwoTable(signCardTable,strJoin,update,"","");		
			//System.out.println(update);
			dao.update(update);
		}catch(Exception e)
		{
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
			kqUtilsClass.dropTable(signCardTable);//删除临时表
		}
	}
	
	/**
	 * 建立一个课程刷卡表，统计一个课程刷卡时间段，到底签到、签退刷了多少次卡
	 * @param classlist
	 * @param classplan
	 * @param dao
	 * @param kqUtilsClass
	 * @return
	 */
    private String signTempCardTable(ArrayList classlist,String classplan,ContentDAO dao,KqUtilsClass kqUtilsClass)
    {
    	String table_name="t#"+this.userView.getUserName()+"_2";	
		table_name=table_name.toLowerCase();
		kqUtilsClass.dropTable(table_name);
		DbWizard dbWizard =new DbWizard(this.getFrameconn());
		Table tables=new Table(table_name);
		Field temp1=new Field("NBase","人员库前缀");
		temp1.setDatatype(DataType.STRING);
		temp1.setLength(3);
		temp1.setKeyable(false);			
		temp1.setVisible(false);
		tables.addField(temp1);		
		temp1=new Field("A0100","人员编号");
		temp1.setDatatype(DataType.STRING);
		temp1.setLength(20);
		temp1.setKeyable(false);			
		temp1.setVisible(false);
		tables.addField(temp1);
		temp1=new Field("b_c","签到");
		temp1.setDatatype(DataType.INT);			
		tables.addField(temp1);
		temp1=new Field("e_c","签退");
		temp1.setDatatype(DataType.INT);			
		tables.addField(temp1);		
		for(int i=0;i<classlist.size();i++)
	    {
			temp1=new Field("b_c_"+i,"签到_"+i);
			temp1.setDatatype(DataType.INT);			
			tables.addField(temp1);
			temp1=new Field("e_c_"+i,"签退_"+i);
			temp1.setDatatype(DataType.INT);			
			tables.addField(temp1);			
	    }
		try
		{
			dbWizard.createTable(tables);
			StringBuffer sql=new StringBuffer();
			sql.append("insert into "+table_name+"(nbase,a0100)");//人员库、人员编号、课程编号、应出勤时间、应出勤次数、正常标记、上课签到次数、下课签退次数
			sql.append("(select nbase,r4001 from R40 where r4013='03' and r4005='"+classplan+"')");
			dao.insert(sql.toString(), new ArrayList());
		}catch(Exception e)
		{
			e.printStackTrace();			
		}
		return table_name;
    }
    private float getMinute(ContentDAO dao,int id){
    	TrainAtteBo tb=new TrainAtteBo();
    	tb.addClassPlanColumn(this.getFrameconn());
    	float minute=60;
    	String sql="select minute from tr_classplan where id="+id;
    	try {
			this.frecset=dao.search(sql);
			if(this.frecset.next()){
				minute=this.frecset.getFloat("minute");
				minute=minute>0?minute:60;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return minute;
	}
	public static void main(String[] args) {

        Date dd=DateUtils.getDate("2010.01.01", "yyyy.MM.dd");
        Date ss=DateUtils.getDate("2010.01.02", "yyyy.MM.dd");
		System.out.println(dd.before(ss));


	}
}

