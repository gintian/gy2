package com.hjsj.hrms.businessobject.train;

import com.hjsj.hrms.businessobject.sys.options.ResourcePopedomParser;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.AdminDb;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import javax.sql.RowSet;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;

public class TrainClassBo {
	private Connection conn=null;
	
	public TrainClassBo(Connection con)
	{
		this.conn = con;
	}
	
	

	/**
	 * 取得某表下的详细信息
	 * @param fieldList
	 * @param userView
	 * @param operatorTableName   r04:教师表 r07:培训资料表 r10：培训场地表
	 * @return
	 */
	public ArrayList getItemFieldList(ArrayList fieldList,String operatorTableName,String id)
	{
		ArrayList list=new ArrayList();
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet rowSet=null;
		try
		{
			rowSet=dao.search("select * from "+operatorTableName+" where "+operatorTableName+"01='"+id+"'");
			rowSet.next();
			
			for(int i=0;i<fieldList.size();i++)
			{
				FieldItem item=(FieldItem)fieldList.get(i);
				if("id".equalsIgnoreCase(item.getItemid())|| "i9999".equalsIgnoreCase(item.getItemid())|| "state".equalsIgnoreCase(item.getItemid()))  //编制日期||状态
                {
                    continue;
                }
				LazyDynaBean abean=new LazyDynaBean();
				abean.set("itemid",item.getItemid());
				abean.set("itemdesc",item.getItemdesc());
				String value="";
				String viewValue="";
				if(rowSet!=null&&!"D".equalsIgnoreCase(item.getItemtype()))
				{
					if("M".equalsIgnoreCase(item.getItemtype())) {
                        value=Sql_switcher.readMemo(rowSet,item.getItemid());
                    } else {
                        value=rowSet.getString(item.getItemid());
                    }
				}
				else if(rowSet!=null&& "D".equalsIgnoreCase(item.getItemtype()))
				{
					SimpleDateFormat format=new SimpleDateFormat("yyyy.MM.dd");
					Date d=rowSet.getDate(item.getItemid());
					if(d==null||d.toString().length()<1){
						value="";
					}else{
						Calendar calendar=Calendar.getInstance();
						calendar.setTime(d);
						calendar.add(Calendar.MONTH,-1);
						value=format.format(calendar.getTime());
					}
				}
				if(!"0".equals(item.getCodesetid()))
				{
					if(value!=null&&value.trim().length()>0){
						if("HJSJ".equals(value)) {
                            viewValue=ResourceFactory.getProperty("jx.khplan.hjsj");
                        } else {
                            viewValue=AdminCode.getCodeName(item.getCodesetid(),value);
                        }
					}
				}
				value=value!=null?value:"";
				abean.set("value",value);
				abean.set("viewvalue",viewValue);
				abean.set("decimalwidth",String.valueOf(item.getDecimalwidth()));
				abean.set("itemtype",item.getItemtype());
				abean.set("itemlength",String.valueOf(item.getItemlength()));
				abean.set("codesetid",item.getCodesetid());
				list.add(abean);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	
	/**
	 * 获取名称
	 * @param operatorTableName  r04:教师表 r07:培训资料表 r10：培训场地表
	 * @param id
	 * @return
	 */
	public String getTitleName(String operatorTableName,String id)
	{
		String titleName="";
		ContentDAO dao=new ContentDAO(this.conn);
		String name="";
		if("r04".equalsIgnoreCase(operatorTableName)) {
            name="r0402";
        } else if("r07".equalsIgnoreCase(operatorTableName)) {
            name="r0702";
        } else if("r10".equalsIgnoreCase(operatorTableName)) {
            name="r1011";
        }
		RowSet rowSet=null;
		try
		{
			rowSet=dao.search("select "+name+" from "+operatorTableName+" where "+operatorTableName+"01='"+id+"'");
			if(rowSet.next())
			{
				titleName=rowSet.getString(name);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return titleName;
		
	}
	
	/**
	 * 申请或撤销 培训班人员
	 * @param operator  add:参加   del：撤销
	 * @param a0100   
	 * @param dbpre
	 * @param r3101   
	 * @return
	 * @throws GeneralException
	 */
	public String operateTrainClassManRecord(String operator,String a0100,String dbpre,String r3101,String state)throws GeneralException
	{
		String info="";
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		RowSet rs2 = null;
		RowSet rs3 = null;
		try{
			RecordVo vo=new RecordVo("r40");			
			SimpleDateFormat dFormat=new SimpleDateFormat("yyyy.MM.dd");
			rs=dao.search("select * from r31 where r3101='"+r3101+"'");
			if(rs.next())
			{
				Date appealStartDate=rs.getDate("r3113");
				Date appealEndDate=rs.getDate("r3114");
				Date classStartDate=rs.getDate("r3115");
				
				if(isOverTime(appealStartDate,appealEndDate)&& "add".equalsIgnoreCase(operator))  //是否超出了报名时间
				{
					info="不在报名时间范围内，不允许再操作！";
				}
				else
				{
					if("add".equals(operator))
					{	
						TrainEffectEvalBo bo = new TrainEffectEvalBo(this.conn, r3101);
						String ctrl_apply = (String) bo.getBean("ctrl_apply", "").get("text");
						if("0".equalsIgnoreCase(ctrl_apply)) {
                            state="03";
                        }
								
						if(rs.getDate("r3115")!=null) {
                            vo.setDate("r4006",rs.getDate("r3115"));
                        }
						if(rs.getDate("r3116")!=null) {
                            vo.setDate("r4007",rs.getDate("r3116"));
                        }
						vo.setDouble("r4008",rs.getDouble("r3112"));
						vo.setString("r4005",r3101);
						vo.setString("r4001",a0100);
						vo.setString("nbase",dbpre);
						
						//这是6标准版本
						//先从业务字典里面查询出该表有哪些字段
						String sql="select itemid from t_hr_busifield where fieldsetid='R40' and useflag=1";
						
						rs=dao.search(sql);
						
						while(rs.next()){
							String item=rs.getString("itemid");
							
							//如果是以下字段就不要 往下查询了
							if("r4006".equalsIgnoreCase(item)||"r4007".equalsIgnoreCase(item)||"r4008".equalsIgnoreCase(item)
									||"r4005".equalsIgnoreCase(item)||"r4001".equalsIgnoreCase(item))
							{
								continue;
							}
							//如果是B0110、E0122
							if("B0110".equalsIgnoreCase(item)||"E0122".equalsIgnoreCase(item) || "E01A1".equals(item)){
								String sqlstr="select "+ item+" from "+dbpre+"A01"+" where a0100="+a0100; 
								rs2=dao.search(sqlstr);
								if(rs2.next()){
									String value=rs2.getString(item);
									//把该指标的值放入r40对应的字段中
									vo.setObject(item.toLowerCase(), value);
								}
								continue;
							}
							//依次遍历剩下的每一个字段，查询出该字段对应的指标集,这里只是查询出以A开头的指标
							String sql2="select fieldsetid from fielditem where itemid='"+item+"' and fieldsetid like 'A%'";
							rs2=dao.search(sql2);
							if(rs2.next()){
								String fieldsetid=rs2.getString("fieldsetid");
								String sql3=null;
								
							   sql3="select "+item+ " from " +dbpre+fieldsetid+" where a0100="+a0100;
							   
							   if(!"A01".equalsIgnoreCase(fieldsetid)){
								   sql3=sql3+" and i9999=(select MAX(i9999) from "+dbpre+fieldsetid+" where A0100="+a0100+")"; 
							   }
								
							   rs3=dao.search(sql3);
								if(rs3.next()){
									Object value=rs3.getObject(item);
									//把该指标的值放入r40对应的字段中
									vo.setObject(item.toLowerCase(), value);
								}
							}
						}
						
						if(isExistRecord("r40","r4001/r4005/nbase",a0100+"/"+r3101+"/"+dbpre))   //是否已经存在该纪录
						{
							dao.update("update r40 set r4013='"+state+"' where r4001='"+a0100+"' and r4005='"+r3101+"' and nbase='"+dbpre+"'");
							info="申请成功！";
						}
						else
						{
							rs=dao.search("select * from "+dbpre+"A01 where a0100='"+a0100+"'");
							if(rs.next())
							{
								vo.setString("r4002",rs.getString("a0101"));
								vo.setString("b0110",rs.getString("b0110"));
								vo.setString("e0122",rs.getString("e0122"));
								vo.setString("r4013",state);
							}
							dao.addValueObject(vo);
							info="申请成功！";
						}
					}
					else  //撤销
					{
						if(isExistRecord("r40","r4001/r4005/nbase",a0100+"/"+r3101+"/"+dbpre))   //是否已经存在该纪录
						{
							dao.delete("delete from r40 where r4001='"+a0100+"' and r4005='"+r3101+"' and nbase='"+dbpre+"'",new ArrayList());
							info="撤销成功!";
						}
						else
						{
							info="您没有申请该培训班!";
						}
					}
				}
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return info;
	}
	
	
	public boolean isOverTime(Date startDate,Date endDate)
	{
		boolean flag=false;
		if(startDate!=null&&endDate!=null)
		{
			Calendar today=Calendar.getInstance();
			Calendar appealStart=Calendar.getInstance();
			Calendar appealEnd=Calendar.getInstance();
			appealStart.setTime(startDate);
			appealEnd.setTime(DateUtils.addDays(endDate,1));
			if(today.before(appealStart)||today.after(appealEnd)) {
                flag=true;
            }
		}else if(startDate==null&&endDate!=null){
			Calendar today=Calendar.getInstance();
			Calendar appealEnd=Calendar.getInstance();
			appealEnd.setTime(DateUtils.addDays(endDate,1));
			if(today.after(appealEnd)) {
                flag=true;
            }
		}else if(startDate!=null&&endDate==null){
			Calendar today=Calendar.getInstance();
			Calendar appealStart=Calendar.getInstance();
			appealStart.setTime(startDate);
			if(today.before(appealStart)) {
                flag=true;
            }
		}
		return flag;
	}
	/**
	 * 培训班是否开始
	 * @param startDate 培训班开始时间
	 * @return false 未开始|true 已开始
	 */
	public boolean isOverStartTime(Date startDate){
		boolean flag=false;
		Calendar today=Calendar.getInstance();
		Calendar appealStart=Calendar.getInstance();
		appealStart.setTime(startDate);
		if(today.after(appealStart)) {
            flag=true;
        }
		return flag;
	}
	
	public boolean isExistRecord(String tableName,String primaryName,String primaryID)
	{
		boolean flag=false;
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try{
			if(primaryName.indexOf("/")==-1) {
                rs=dao.search("select * from "+tableName+" where "+primaryName+"='"+primaryID+"'");
            } else
			{
				String[] primaryNames=primaryName.split("/");
				String[] primaryIDs=primaryID.split("/");
				StringBuffer sql=new StringBuffer("select * from "+tableName+" where ");
				StringBuffer subSql=new StringBuffer("");
				for(int i=0;i<primaryNames.length;i++)
				{
					subSql.append(" and  "+primaryNames[i]+"='"+primaryIDs[i]+"'");
				}
				sql.append(subSql.substring(4));
				rs=dao.search(sql.toString());
			}
			if(rs.next()) {
                flag=true;
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return flag;
	}
	
	/**
	 * 提供精确的小数位四舍五入处理。
	 * 
	 * @param v
	 *            需要四舍五入的数字
	 * @param scale
	 *            小数点后保留几位
	 * @return 四舍五入后的结果
	 */
	public String round(String v, int scale) {

		if (scale < 0) {
			throw new IllegalArgumentException(
					"The scale must be a positive integer or zero");
		}
		BigDecimal b = new BigDecimal(v);
		BigDecimal one = new BigDecimal("1");
		return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).toString();
	}
	
	/**
	 * 培训班详细信息
	 * @param r3101
	 * @return
	 * @throws GeneralException
	 */
	public LazyDynaBean getTrainClassDesc(String r3101)throws GeneralException
	{
		LazyDynaBean abean=new LazyDynaBean();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try{
			SimpleDateFormat dFormat=new SimpleDateFormat("yyyy.MM.dd HH:mm");
			
			FieldItem r3108F = DataDictionary.getFieldItem("r3108", "r31");
			FieldItem r3109F = DataDictionary.getFieldItem("r3109", "r31");
			FieldItem r3112F = DataDictionary.getFieldItem("r3112", "r31");
			int dw = r3112F.getDecimalwidth();
			StringBuffer sql=new StringBuffer("select r3130,(select count(*) from r40 where r4005='"+r3101+"' and r4013='03') appealCount,r3115,r3116,r3112,r3111,");
						 sql.append(" r10.r1011,r10.r1001,r3122,r3117,r3126");
						 if(r3108F != null && "1".equals(r3108F.getUseflag())) {
                             sql.append(" ,r3108 ");
                         }
						 if(r3109F != null && "1".equals(r3109F.getUseflag())) {
                             sql.append(" ,r3109 ");
                         }
						 sql.append(" from r31 left join r10 on r31.r3126=r10.r1001  where r31.r3101='"+r3101+"'");
             rs=dao.search(sql.toString());
             if(rs.next())
             {
            	 abean.set("r3101",r3101);
            	 String r3130=rs.getString("r3130");
            	 r3130=r3130!=null?r3130:"";
            	 r3130=r3130.replaceAll("%26lt;","<").replaceAll("%26gt;",">").replaceAll("&lt;","<").replaceAll("&gt;",">");
            	 abean.set("r3130",r3130);
            	 abean.set("appealCount",rs.getString("appealCount"));
            	 Timestamp startDate =rs.getTimestamp("r3115");
            	 Timestamp endDate=rs.getTimestamp("r3116");
            	 String a_startDate="";
            	 String a_endDate="";
            	 if(startDate!=null) {
                     a_startDate=dFormat.format(startDate);  //起始时间
                 }
            	 if(endDate!=null) {
                     a_endDate=dFormat.format(endDate);		//结束时间
                 }
            	 abean.set("startDate",a_startDate);
            	 abean.set("endDate",a_endDate);
            	 
            	 String r3112="";
            	 if(r3112F != null && "1".equals(r3112F.getUseflag()) && rs.getString("r3112")!=null) {
                     r3112= rs.getString("r3112");
                 }
            	 r3112 = PubFunc.round(r3112, dw);
            	 abean.set("r3112",r3112);  //培训学时
            	 String r3108="";
            	 if(r3108F != null && "1".equals(r3108F.getUseflag()) && rs.getString("r3108")!=null){
            		 r3108= rs.getString("r3108");
            	 }
            	 abean.set("r3108",r3108);  //培训说明
            	 String r3109="";
            	 if(r3109F != null && "1".equals(r3109F.getUseflag()) && rs.getString("r3109")!=null){
            		 r3109= rs.getString("r3109");
            	 }
            	 abean.set("r3109",r3109);  //培训对象
            	 String r3126="";
            	 if(rs.getString("r3126")!=null) {
                     r3126= rs.getString("r3126");
                 }
            	 abean.set("r3126",r3126);  //培训地点
            	 String r3111="";
            	 if(rs.getString("r3111")!=null) {
                     r3111= rs.getString("r3111");
                 }
            	 abean.set("r3111",r3111);  //培训费用
            	 if(rs.getString("r1011")!=null) {
                     abean.set("r1011",rs.getString("r1011"));  //场地名称
                 } else {
                     abean.set("r1011","");
                 }
            	 
            	 if(rs.getString("r1001")!=null) {
                     abean.set("r1001",rs.getString("r1001"));
                 } else {
                     abean.set("r1001","");
                 }
            	 String desc=Sql_switcher.readMemo(rs,"r3122");
            	 desc=desc.replaceAll("\r\n","<Br>");
            	 abean.set("r3122",desc);
            	 String r3117=Sql_switcher.readMemo(rs,"r3117");
            	 r3117=r3117.replaceAll("\r\n","<Br>");
            	 abean.set("r3117",r3117);
            	 
            	 abean.set("itemList",getTrainClassCourseList(r3101));
             }
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return abean;
	}
	
	/**
	 * 取得培训班课程列表信息
	 * @param r3101
	 * @return
	 */
	public ArrayList getTrainClassCourseList(String r3101)throws GeneralException
	{
		ArrayList list=new ArrayList();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try{
			String sql="select r13.r1302,r0401,r0402,r4112,r0701,r0702" 
					+" from R41 left join R13 on r41.r4105=r13.r1301 left join R04 on r41.r4106=r0401 left join R07 on r41.r4114=r07.r0701 where r41.r4103='"+r3101+"'";
			rs=dao.search(sql);
			while(rs.next())
			{
				LazyDynaBean a_bean=new LazyDynaBean();
				if(rs.getString("r1302")!=null) {
                    a_bean.set("r1302",rs.getString("r1302"));
                } else {
                    a_bean.set("r1302","");
                }
				a_bean.set("r0401",rs.getString("r0401"));
				if(rs.getString("r0402")!=null) {
                    a_bean.set("r0402",rs.getString("r0402"));
                } else {
                    a_bean.set("r0402","");
                }
				if(rs.getString("r4112")!=null) {
                    a_bean.set("r4112",rs.getString("r4112"));
                } else {
                    a_bean.set("r4112","");
                }
				a_bean.set("r0701",rs.getString("r0701"));
				if(rs.getString("r0702")!=null) {
                    a_bean.set("r0702",rs.getString("r0702"));
                } else {
                    a_bean.set("r0702","");
                }
				list.add(a_bean);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return list;
	}
	/**
	 * 获取项目名称
	 * @param r4105
	 * @return
	 * @throws GeneralException
	 */
	public static String getProgrammeName(String r4105) throws GeneralException {
		String ProgrammeName = "";
		Connection conn = null;
		RowSet rs = null;
		String sql = "select r1302 from r13 where r1301='" + r4105 + "'";
		try {
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search(sql);
			if (rs.next()) {
				ProgrammeName = rs.getString("r1302");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
        	PubFunc.closeResource(conn);
		}
		return ProgrammeName;
	}
	/**
	 * 获取教师名称
	 * @param r4106
	 * @return
	 * @throws GeneralException
	 */
	public static String getTeacherName(String r4106) throws GeneralException {
		String TeacherName = "";
		Connection conn = null;
		RowSet rs = null;
		String sql = "select r0402 from r04 where r0401='" + r4106 + "'";
		try {
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search(sql);
			if (rs.next()) {
				TeacherName = rs.getString("r0402");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
        	PubFunc.closeResource(conn);
		}
		return TeacherName;
	}
	/**
	 * 获取资料名称
	 * @param r4114
	 * @return
	 * @throws GeneralException
	 */
	public static String getDataName(String r4114) throws GeneralException {
		String DataName = "";
		Connection conn = null;
		RowSet rs = null;
		String sql = "select r0702 from r07 where r0701='" + r4114 + "'";
		try {
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search(sql);
			if (rs.next()) {
				DataName = rs.getString("r0702");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
        	PubFunc.closeResource(conn);
		}
		return DataName;
	}
	/**
	 * 判断代码类是否是一门课程，若是获取课程id
	 * @param lessons
	 * @return
	 * @throws GeneralException
	 */
	public static String getLessinsCode(String lessons) throws GeneralException {
		String code = "";
		Connection conn = null;
		RowSet rs = null;
		String sql = "select r5000 from r50 where codeitemid='"+lessons+"'";
		try{
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search(sql);
			if(rs.next()) {
                code=rs.getString("r5000");
            }
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(rs);
        	PubFunc.closeResource(conn);
		}
		return code;
	}
	/**
	 * 检查培训班中的培训学员是否满员
	 * @param classid 培训班编号
	 * @return true 满额|false 未满
	 */
	public static boolean checkStudentsCount(String classid){
		boolean flag = false;
		int r3110=0;
		int studentcount=0;
		RowSet rs=null;
		String sql = "select r3110 from r31 where r3101='"+classid+"'";
		String sqls="select count(R4001)  person from R40 where R4005='"+classid+"' and r4013='03'";
		Connection conn = null;
		try {
			conn = AdminDb.getConnection();
			TrainEffectEvalBo bo = new TrainEffectEvalBo(conn, classid);
			String ctrl_count = (String) bo.getBean("ctrl_count", "").get("text");
			if (!"1".equalsIgnoreCase(ctrl_count)) {
                return flag;
            }
			ContentDAO dao = new ContentDAO(conn);
			rs=dao.search(sql);
			if(rs.next()) {
                r3110=rs.getInt("r3110");
            }
			rs=dao.search(sqls);
			if(rs.next()) {
                studentcount=rs.getInt("person");
            }
			if(studentcount>=r3110&&r3110!=0) {
                flag=true;
            }
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(rs);
        	PubFunc.closeResource(conn);
		}
		return flag;
	}
	/**
	 * 获取培训班审核公式
	 * @return
	 */
	public ArrayList getPxFormulaList()
	{
		ArrayList list = new ArrayList();
		try
		{
			StringBuffer sql = new StringBuffer();
			sql.append("select chkid,name,validflag from hrpchkformula where flag=7 order by chkid");
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs=dao.search(sql.toString());
			while(rs.next())
			{
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("chkid",rs.getString("chkid"));
				bean.set("name",rs.getString("name"));
				bean.set("validflag", rs.getString("validflag"));
				list.add(bean);
			}
			rs.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 获取培训班已启用的审核公式
	 * @return
	 */
	public ArrayList getPxTrainFormulaList()
	{
		ArrayList list = new ArrayList();
		try
		{
			StringBuffer sql = new StringBuffer();
			sql.append("select * from hrpchkformula where flag=7 and validflag=1 order by chkid");
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs=dao.search(sql.toString());
			while(rs.next())
			{
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("chkid",rs.getString("chkid"));
				bean.set("name",rs.getString("name"));
				bean.set("validflag", rs.getString("validflag"));
				bean.set("formula", Sql_switcher.readMemo(rs,"formula"));
				bean.set("information",Sql_switcher.readMemo(rs, "information"));
				list.add(bean);
			}
			rs.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 获取审核公式中的公式名称和审核提示
	 * @param chkid 公式id
	 * @return
	 */
	public LazyDynaBean getFormulaInfo(String chkid)
	{
		LazyDynaBean bean = new LazyDynaBean();
		try
		{
			String sql = "select name,information from hrpchkformula where chkid="+chkid;
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search(sql);
			while(rs.next())
			{
				bean.set("name", rs.getString("name"));
				bean.set("information",Sql_switcher.readMemo(rs,"information"));
			}
			rs.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return bean;
	}
	/**
	 * 获取审核公式中的表达式
	 * @param chkid 公式id
	 * @return
	 */
	public String getPxFormula(String chkid)
	{
		String formula="";
		try
		{
			String sql = "select formula from hrpchkformula where chkid="+chkid;
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search(sql);
			while(rs.next())
			{
				formula=Sql_switcher.readMemo(rs, "formula");
			}
			rs.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return formula;
	}
	
	/**
	 * 过滤培训班审核公式中用不到的指标
	 * @param formulaList 启用的培训班审核公式列表
	 * @return ArrayList 过滤后的指标
	 * @throws GeneralException
	 */
	public ArrayList getTrainMidVariableList(ArrayList formulaList) throws GeneralException
	{
		ArrayList fieldlist=new ArrayList();
		ArrayList new_fieldList=new ArrayList();
		fieldlist = DataDictionary.getFieldList("R31", com.hrms.hjsj.sys.Constant.USED_FIELD_SET);
		
		FieldItem item=null;
		HashMap map=new HashMap();
		for(int i=0;i<formulaList.size();i++)
		{
			  LazyDynaBean bean = (LazyDynaBean)formulaList.get(i);
			  String formula=((String)bean.get("formula")).toLowerCase();
			  if(formula==null|| "".equals(formula)) {
                  continue;
              }
              for(int j=0;j<fieldlist.size();j++)
              {
            	  item=(FieldItem)fieldlist.get(j);
            	  String item_id=item.getItemid().toLowerCase();
            	  String item_desc=item.getItemdesc().trim().toLowerCase();
            	  if(formula.indexOf(item_desc)!=-1&&map.get(item_id)==null)
            	  {
            		  new_fieldList.add(item);
            		  map.put(item_id, "1");
            	  }
            		  
              }
		}
		return new_fieldList;
	}
	/**
	 * 将培训班已启用的审核公式转换为where条件
	 * @param formulaList 审核公式列表
	 * @param userView
	 * @return
	 */
	public String getWherestr(ArrayList formulaList, UserView userView) {
		String wherestr = "";
		String checkinfor = "";
		try {
			ArrayList midVariableList;
			ArrayList varlist = new ArrayList();
			midVariableList = this.getTrainMidVariableList(formulaList);
			varlist.addAll(midVariableList);

			for (int i = 0; i < formulaList.size(); i++) {
				LazyDynaBean bean = (LazyDynaBean) formulaList.get(i);
				String formula = (String) bean.get("formula");
				String formulaname = (String) bean.get("name");
				if (formula == null || "".equals(formula)) {
                    continue;
                }
				YksjParser yp = null;

				yp = new YksjParser(userView, varlist, YksjParser.forNormal,
						YksjParser.LOGIC, YksjParser.forPerson, "", "");
				yp.setCon(this.conn);
				boolean b = yp.Verify_where(formula.trim());
				if (!b) {
					checkinfor = formulaname + ResourceFactory.getProperty("workdiary.message.review.failure") + "!\n\n";
					checkinfor += yp.getStrError();
					throw GeneralExceptionHandler.Handle(new Exception(checkinfor));
				}
				yp.setVerify(false);
				yp.run(formula.trim());
				wherestr = " (" + yp.getSQL() + ") or";// 公式的结果
			}
			
			wherestr=wherestr.substring(0, wherestr.length()-3);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return wherestr;
	}
	
	/**
	 * 培训班审核结果导出excel获取文字样式
	 * @param workbook
	 * @param styles
	 * @return
	 */
	public HSSFCellStyle style(HSSFWorkbook workbook,int styles){
		HSSFCellStyle style = workbook.createCellStyle();
		
		
		switch (styles) {

		case 0:
			HSSFFont fonttitle = fonts(workbook, ResourceFactory.getProperty("gz.gz_acounting.black.font"), 15);
			fonttitle.setBold(true);;// 加粗
			style.setFont(fonttitle);
			style.setBorderBottom(BorderStyle.NONE);
			style.setBorderLeft(BorderStyle.NONE);
			style.setBorderRight(BorderStyle.NONE);
			style.setBorderTop(BorderStyle.NONE);
			style.setAlignment(HorizontalAlignment.CENTER);
			break;
		case 1:
			style.setFont(fonts(workbook, ResourceFactory.getProperty("gz.gz_acounting.m.font"), 12));
			style.setBorderBottom(BorderStyle.THIN);
			style.setBorderLeft(BorderStyle.THIN);
			style.setBorderRight(BorderStyle.THIN);
			style.setBorderTop(BorderStyle.THIN);
			style.setVerticalAlignment(VerticalAlignment.CENTER);
			style.setAlignment(HorizontalAlignment.CENTER);
			break;
		case 2:
			style.setFont(fonts(workbook, ResourceFactory.getProperty("gz.gz_acounting.m.font"), 12));
			style.setBorderBottom(BorderStyle.THIN);
			style.setBorderLeft(BorderStyle.THIN);
			style.setBorderRight(BorderStyle.THIN);
			style.setBorderTop(BorderStyle.THIN);
			style.setAlignment(HorizontalAlignment.LEFT);
			style.setFillForegroundColor(HSSFColor.GREY_40_PERCENT.index);
			style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			break;
		case 3:
			style.setFont(fonts(workbook, ResourceFactory.getProperty("gz.gz_acounting.m.font"), 12));
			style.setBorderBottom(BorderStyle.NONE);
			style.setBorderLeft(BorderStyle.NONE);
			style.setBorderRight(BorderStyle.NONE);
			style.setBorderTop(BorderStyle.NONE);
			break;
		case 4:
			style.setFont(fonts(workbook, ResourceFactory.getProperty("gz.gz_acounting.m.font"), 12));
			style.setBorderBottom(BorderStyle.THIN);
			style.setBorderLeft(BorderStyle.NONE);
			style.setBorderRight(BorderStyle.NONE);
			style.setBorderTop(BorderStyle.NONE);
			break;
		default:
			style.setFont(fonts(workbook, ResourceFactory.getProperty("gz.gz_acounting.m.font"), 12));
			style.setAlignment(HorizontalAlignment.LEFT);
			style.setBorderBottom(BorderStyle.THIN);
			style.setBorderLeft(BorderStyle.THIN);
			style.setBorderRight(BorderStyle.THIN);
			style.setBorderTop(BorderStyle.THIN);
			break;
		}
	return style;
	}
	public HSSFFont fonts(HSSFWorkbook workbook,String fonts,int size){
		HSSFFont font = workbook.createFont();
		font.setFontHeightInPoints((short)size);
		font.setFontName(fonts);
		return font;
	}
	/**
	 * 获取培训班年度列表
	 * @return yearList
	 */
	public ArrayList getYearList() throws GeneralException {
		ArrayList yearList = new ArrayList();
		RowSet rs = null;
		CommonData cd = new CommonData();
		
		int year = 0;

		StringBuffer sql = new StringBuffer();
		sql.append("SELECT DISTINCT " + Sql_switcher.year("R3115") + " AS year");
		sql.append(" FROM R31 WHERE R3115 IS NOT NULL OR R3115<>'' ORDER BY year");

		ContentDAO dao = new ContentDAO(this.conn);

		try {
			rs = dao.search(sql.toString());

			while (rs.next()) {
				cd = new CommonData();
				year = rs.getInt("year");
				cd.setDataName(String.valueOf(year));
				cd.setDataValue(String.valueOf(year));
				yearList.add(cd);
			}
			Calendar ca = Calendar.getInstance();
			int curyear = ca.get(Calendar.YEAR);

			// 没有申请记录或申请都在当前年度之前，加入当前年度
			if (year < curyear) {
				cd = new CommonData();
				cd.setDataName(String.valueOf(curyear));
				cd.setDataValue(String.valueOf(curyear));
				yearList.add(cd);
			}
			cd = new CommonData();
			cd.setDataName(ResourceFactory.getProperty("train.job.all.year"));
			cd.setDataValue(ResourceFactory.getProperty("train.job.all.year"));
			yearList.add(cd);
			Collections.reverse(yearList);
		} catch (SQLException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally{
			PubFunc.closeResource(rs);
		}
		return yearList;
	}
	/**
	 * 获取培训计划的名称
	 * @param planid
	 * @return
	 * @throws GeneralException
	 */
	public static String getPlanName(String planid) throws GeneralException {
		String planName = "";
		Connection conn = AdminDb.getConnection();
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(conn);
		String sql = "select  r2502 from r25 where r2501='"+planid+"'";
		try {
			
			rs = dao.search(sql.toString());
			if (rs.next()) {
				planName = rs.getString("r2502");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally{
			PubFunc.closeResource(rs);
			PubFunc.closeResource(conn);
			
		}
		
		return planName;
	}
	
	/**
	 * 获取培训学员审批状态列表
	 * @return arrayList
	 */
	public ArrayList getR40sp_flag(){
		ArrayList codelist = new ArrayList();
		
		CodeItem ci = new CodeItem();
		ci.setCodeitem("");
		ci.setCodeid("");
		ci.setCodename(ResourceFactory.getProperty("label.all"));
		codelist.add(ci);
		
		ci = AdminCode.getCode("23", "08");
		codelist.add(ci);
		ci = AdminCode.getCode("23", "02");
		codelist.add(ci);
		ci = AdminCode.getCode("23", "03");
		codelist.add(ci);
		ci = AdminCode.getCode("23", "07");
		codelist.add(ci);
		
		
		return codelist;
	}
	/**
	 * 将关联表类型的指标的codesetid还原为初始值
	 * @param list
	 * @throws GeneralException
	 */
	public static void setcodesetid(ArrayList list) throws GeneralException {
		for(int i = 0; i<list.size();i++){
			CommonData cd = (CommonData)list.get(i);
			FieldItem fi = DataDictionary.getFieldItem(cd.getDataName());
			fi.setCodesetid(cd.getDataValue());
		}
	}
	/**
	 * 判断是否过期
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static boolean isOverTimes(String startTime,String endTime)
	{
		boolean flag=false;
		try {
			java.util.Date startDate = null;
			java.util.Date endDate = null;
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
			if (startTime != null && startTime.length() >= 10) {
                startDate = (java.util.Date) dateFormat.parse(startTime);
            }
			if (endTime != null && endTime.length() >= 10) {
                endDate = (java.util.Date) dateFormat.parse(endTime);
            }
			if (startDate != null && endDate != null) {
				Calendar today = Calendar.getInstance();
				Calendar appealStart = Calendar.getInstance();
				Calendar appealEnd = Calendar.getInstance();
				appealStart.setTime(startDate);
				appealEnd.setTime(DateUtils.addDays(endDate, 1));
				if (today.before(appealStart) || today.after(appealEnd)) {
                    flag = true;
                }
			} else if (startDate == null && endDate != null) {
				Calendar today = Calendar.getInstance();
				Calendar appealEnd = Calendar.getInstance();
				appealEnd.setTime(DateUtils.addDays(endDate, 1));
				if (today.after(appealEnd)) {
                    flag = true;
                }
			} else if (startDate != null && endDate == null) {
				Calendar today = Calendar.getInstance();
				Calendar appealStart = Calendar.getInstance();
				appealStart.setTime(startDate);
				if (today.before(appealStart)) {
                    flag = true;
                }
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}
	/**
	 * 判断用户的资源权限中是否有该培训班
	 * @param userview
	 * @param r3101
	 * @return
	 */
	public static boolean isHaveRes(UserView userview, String r3101){
		boolean flag = true;
		if(!userview.isHaveResource(ResourcePopedomParser.TRAINJOB, r3101)) {
            flag = false;
        }
		return flag;
		
	}
	/**
	 * 判断用户有没有附件
	 * @param r4114
	 * @return
	 * @throws GeneralException
	 */
	public static boolean haveFile(String r4114) throws GeneralException {
		Connection conn = null;
		RowSet rs = null;
		boolean has = false;
		try {
			String sql = "select * from tr_res_file where r0701='"+r4114+"' and type='0' ";
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search(sql);
			if (rs.next()) {
				has = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
        	PubFunc.closeResource(conn);
		}
		return has;
	}
	/**
	 * 强制替换查询条件语句中的表名（新的表中必需有对应的指标）
	 * @param tableName 新的表名
	 * @param sqlWhere  查询条件语句
	 * @param column    需要替换表名的指标
	 * @return sqlWhere
	 */
    public String getSqlWhere(String tableName, String sqlWhere, String column) {
        if (tableName == null || tableName.length() < 1) {
            return sqlWhere;
        }
        if (sqlWhere == null || sqlWhere.length() < 1) {
            return sqlWhere;
        }
        if (column == null || column.length() < 1) {
            return sqlWhere;
        }

        String[] columns = column.split(",");
        for (int i = 0; i < columns.length; i++) {
            FieldItem fi = DataDictionary.getFieldItem(columns[i]);
            FieldItem item = DataDictionary.getFieldItem(columns[i], tableName);
            String name = fi.getFieldsetid();

            if (item != null && sqlWhere.indexOf(name) > -1) {
                sqlWhere = sqlWhere.replaceAll(name, tableName);
            }
        }

        return sqlWhere;
    }
    /**
     * 检测是否是权限范围内的培训班
     * @param classid 培训班编号
     * @param userView 当前用户
     * @return
     */
    public boolean checkClassPiv(String classid, UserView userView) {
        boolean flag = false;
        if (userView.isSuper_admin()) {
            flag = true;
        } else {
            RowSet rs = null;
            try {
            TrainCourseBo bo = new TrainCourseBo(userView);
            String a_code = bo.getUnitIdByBusi();
            
            TransDataBo tbo = new TransDataBo();
            String where = tbo.sqlWhere(null, a_code, null, null);
            ContentDAO dao = new ContentDAO(conn);
            String sql ="select 1 " + where + " and r3101='" + classid + "'";
            
                rs = dao.search(sql);
                if(rs.next()) {
                    flag = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                PubFunc.closeResource(rs);
            }
        }
        return flag;
    }
    /**
     * * 检测是否是权限范围内的人员
     * @param a0100  人员编号
     * @param dbname 人员库
     * @param userView 当前用户
     * @return
     */
    public boolean checkPersonPiv(String a0100, String dbname, UserView userView) {
        boolean flag = false;
        if (userView.isSuper_admin()) {
            flag = true;
        } else {
            String sql = "select 1 from " + dbname + "a01 where a0100='" + a0100 + "' ";
            String whereStr = TrainCourseBo.getUnitIdByBusiWhere(userView);
            sql += whereStr.replaceFirst("where", "and");
            RowSet rs = null;
            ContentDAO dao = new ContentDAO(conn);
            try {
                rs = dao.search(sql);
                if(rs.next()) {
                    flag = true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                if(rs != null) {
                    rs.close();
                }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return flag;
    }
    /**
     * 校验日期类型数据填写是否符合基本规则
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return
     */
    public static boolean checkDate(String startTime,String endTime) {
        boolean flag=false;
        try {
            java.util.Date startDate = null;
            java.util.Date endDate = null;
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            if (startTime != null && startTime.length() >= 10) {
                startDate = (java.util.Date) dateFormat.parse(startTime);
            }
            
            if (endTime != null && endTime.length() >= 10) {
                endDate = (java.util.Date) dateFormat.parse(endTime);
            }
            
            if (startDate != null && endDate != null) {
                Calendar appealStart = Calendar.getInstance();
                Calendar appealEnd = Calendar.getInstance();
                appealStart.setTime(startDate);
                appealEnd.setTime(endDate);
                
                if (appealStart.before(appealEnd)) {
                    flag = true;
                }
            } 
        }catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }
    /**
     * 校验培训班的报名时间与起止时间
     * @param r3130
     * @param stratdate 报名开始时间
     * @param enddate   报名结束时间
     * @param datestr   培训班开始时间
     * @param dateend   培训班结束时间
     * @return
     */
    public static String checkClassDate(String r3130, String stratdate,String enddate,String datestr,String dateend) {
        String flag = "true";
        try {
            String r3113dec = DataDictionary.getFieldItem("r3113", "r31").getItemdesc();
            String r3114dec = DataDictionary.getFieldItem("r3114", "r31").getItemdesc();
            String r3115dec = DataDictionary.getFieldItem("r3115", "r31").getItemdesc();
            String r3116dec = DataDictionary.getFieldItem("r3116", "r31").getItemdesc();
           
            //判断报名开始时间是否早于报名结束时间
            if(stratdate !=null && stratdate.length() > 0 && enddate !=null && enddate.length() > 0){
                if(checkDate(enddate, stratdate)){
                    flag = "<" + r3130 + ">" + ResourceFactory.getProperty("train.info.class.date.de") + r3114dec + ResourceFactory.getProperty("train.info.class.date.later") + r3113dec + "！";
                    return flag;
                }
            }
            //判断培训班开始时间是否早于培训班结束时间
            if(datestr !=null && datestr.length() > 0 && dateend !=null && dateend.length() > 0){
                if(checkDate(dateend, datestr)){
                    flag = "<" + r3130 + ">" + ResourceFactory.getProperty("train.info.class.date.de") + r3116dec + ResourceFactory.getProperty("train.info.class.date.later") + r3115dec + "！";
                    return flag;
                }
            }
            //判断报名结束时间是否晚于报名结束时间
            if(enddate !=null && enddate.length() > 0 && dateend !=null && dateend.length() > 0){
                if(!checkDate(enddate, dateend)){
                    flag = "<" + r3130 + ">" + ResourceFactory.getProperty("train.info.class.date.de") + r3116dec + ResourceFactory.getProperty("train.info.class.date.later") + r3114dec + "！";
                    return flag;
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }
    
    public static String checkIsFillable(RecordVo vo) {
        String flag = "";
        ArrayList fieldList = DataDictionary.getFieldList("r31", Constant.USED_FIELD_SET);
        for(int i = 0; i <fieldList.size(); i++){
            FieldItem fi = (FieldItem) fieldList.get(i);
            if(fi.isFillable()){
                String value = vo.getString(fi.getItemid());
                if(value == null || value.length() < 1) {
                    flag += "\r\n" + fi.getItemdesc() + ResourceFactory.getProperty("train.job.isfillable");
                }
            }
                
        }
        
        return flag;
    }
    /**
     * 获取不在权限范围内的培训班
     * @param ids 培训班编号
     * @param conn 数据库链接
     * @param where 权限范围内的单位拼接的where条件
     * @return 不在权限内的培训班名称
     */
    public static String checkclass(String ids,Connection conn,String where) {
        String classname = "";
        String id = "";
        ArrayList idlist = new ArrayList();
        try {
            
            if (ids == null || ids.length() < 1) {
                return classname;
            }
            
            String[] classids = ids.split(",");

            for (int i = 0; i < classids.length; i++) {
                if (classids[i] == null || classids[i].length() < 1) {
                    continue;
                }
                String[] cids = classids[i].split(":");

                if (cids[0] == null || cids[0].length() < 1) {
                    continue;
                }

                id += ",'" + cids[0] + "'";
                
                if(i % 1000 == 999){
                    idlist.add(id);
                    id = "";
                }
            }
            
            if(id.length() > 0) {
                idlist.add(id);
            }

            if (idlist == null || idlist.size() < 1) {
                return classname;
            }
            
            if(where !=null && where.length()>0){
                where = where.replaceAll("like", "not like").replaceAll("or b0110='HJSJ'", "and b0110<>'HJSJ'");
                where = where.replace("1=1", "1=2");
            }
            
            RowSet rs = null;
            ContentDAO dao = new ContentDAO(conn);
            
            for(int i = 0; i < idlist.size(); i++){
                String cid = (String) idlist.get(i);
                rs = dao.search("select r3130 from r31 where r3101 in (" + cid.substring(1) + ") " + where);
                while(rs.next()){
                    String name = rs.getString("r3130");
                    classname += ",[" + name + "]";
                }
            
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if(classname != null && classname.length() >0) {
            classname = classname.substring(1);
        }
        
        return classname;

    }
    /**
     * 校验培训班中的数值类型是否是负数
     * @param vo 前台传递的培训班各指标的值
     * @return
     */
    public static String CheckNumber(RecordVo vo) {
        
        ArrayList fieldlist = DataDictionary.getFieldList("r31", Constant.USED_FIELD_SET);
        if(fieldlist == null || fieldlist.size() < 1) {
            return "true";
        }
        
        StringBuffer flag = new StringBuffer();
        for(int i = 0; i < fieldlist.size(); i++ ){
            FieldItem fi = (FieldItem) fieldlist.get(i);
            String state = fi.getState();
            
            if("0".equalsIgnoreCase(state)) {
                continue;
            }
            
            String itemtype = fi.getItemtype();
            if(!"N".equalsIgnoreCase(itemtype)) {
                continue;
            }
            
            int value = vo.getInt(fi.getItemid());
            
            if (value < 0) {
                flag.append("\r\n<" + vo.getString("r3130") + ">");
                flag.append(ResourceFactory.getProperty("train.job.class.zhong"));
                flag.append("[" + fi.getItemdesc() + "]");
                flag.append(ResourceFactory.getProperty("train.job.class.numerror"));
            }
            
        }
        String msg = "true";
        if(flag != null && flag.length() > 0) {
            msg= flag.toString();
        }
        
        return msg;
    }

}
