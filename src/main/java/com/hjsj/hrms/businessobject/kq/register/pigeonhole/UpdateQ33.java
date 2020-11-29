package com.hjsj.hrms.businessobject.kq.register.pigeonhole;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.interfaces.KqConstant;
import com.hjsj.hrms.businessobject.kq.interfaces.KqDBHelper;
import com.hjsj.hrms.businessobject.kq.kqself.AnnualApply;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.module.kq.application.KqOverTimeForLeaveBo;
import com.hjsj.hrms.utils.OperateDate;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class UpdateQ33
{
    private UserView userView;
    private Connection conn;
    private KqDBHelper dbHelper;
    private HashMap kqItems;
    /**
     * 表单申请开始日期  //48612 由于部分调休假接口扣减时都是按照当前时间范围计算，导致扣减失败 故增加该参数
     */
    private Date startDate = new Date();

    private final static long MIN = 60L * 1000L;
    
    public UpdateQ33(UserView userView, Connection conn)
    {
        super();
        this.userView = userView;
        this.conn = conn;
        this.dbHelper = new KqDBHelper(conn);
        if (this.kqItems == null) {
            this.kqItems = new HashMap();
        }
    }

    private UpdateQ33()
    {
        super();
        if (this.kqItems == null) {
            this.kqItems = new HashMap();
        }
    }
    /**
     * 更新调休加班明细表
     * @param kq_duration
     * @param nbase
     * @throws GeneralException
     */
    public void updateQ33(String kq_duration,String nbase) throws GeneralException{
        updateQ33(kq_duration, nbase, "");
    }
    /**
     * 更新调休加班明细表
     * @param kq_duration
     * @param nbase
     * @throws GeneralException
     */
    public void updateQ33(String kq_duration,String nbase,String temp_table) throws GeneralException{
        String content = KqParam.getInstance().getOVERTIME_FOR_LEAVETIME();
        if(content == null || "".equals(content)) {
            return;
        }
        
        String array[] = content.split(",");
        ArrayList forLeaveTime = new ArrayList();
        StringBuffer str = new StringBuffer();
        for(int i = 0;i<array.length;i++){
            String fieldItemId = getFieldvalue("fielditemid",array[i],"item_id");
            if(fieldItemId != null && !"".equals(fieldItemId.trim())) {
                forLeaveTime.add(fieldItemId);
                str.append(fieldItemId + ",");
            }
        }
        
        String overForOff = KqUtilsClass.getFieldByDesc("Q11", "是否调休");
        if (overForOff != null && overForOff.length() > 0) 
        {
            String fieldsString = getFieldvalue("fielditemid","调休加班","item_name");
            if (fieldsString != null && fieldsString.length() > 0) 
            {
                //zxj 20141218 考勤规则id也需要重新取
                array = new String[1];
                array[0] = getFieldvalue("item_id", "调休加班", "item_name");

                forLeaveTime = new ArrayList();
                str.setLength(0);
                forLeaveTime.add(fieldsString);
                str.append(fieldsString + ",");
            }
        }
        
        //zxj 20170803 没有加班指标
        if(str.length() <= 0) {
            return;
        }
        
        str.deleteCharAt(str.length()-1);
        
        //没有用来调休的加班类型
        if(forLeaveTime.size() == 0) {
            return;
        }
        
        ArrayList dateList = new ArrayList();
        String startDate = "";
        String endDate = "";
        if (kq_duration.indexOf("`") != -1) {
            String[] date = kq_duration.split("`");
            startDate = date[0];
            endDate = date[1];
        }else {
            dateList = RegisterDate.getKqDate(this.conn, kq_duration);
            startDate = (String)dateList.get(0);
            endDate = (String)dateList.get(dateList.size()-1);
        }
        String dateWhr = "Q03Z0>='" + startDate + "' and " + "Q03Z0<='" + endDate + "'";
        
        StringBuffer jointable1 = new StringBuffer(); 
        StringBuffer jointable2 = new StringBuffer();
        StringBuffer condition = new StringBuffer();
        StringBuffer delCondition = new StringBuffer();
        
        String sqlwhere = RegisterInitInfoData.getWhereINSql(this.userView, nbase);
        
        jointable1.append("select Q.A0100,Q.nbase,Q03Z0,B0110,E0122,E01A1,A0101,"+str+" from Q03 Q ");
        
        jointable2.append("select A0100 " + sqlwhere);
        if (temp_table.length() > 0) //手工将数据处理的结果确认到日明细的同时更新Q33，人员是数据处理结果表里面的人
        {
            if (jointable2.indexOf("WHERE") != -1) {
                jointable2.append(" AND ");
            } else {
                jointable2.append(" WHERE ");
            }
            
            jointable2.append(" a0100 in ( select a0100 from " + temp_table + " where nbase='" + nbase + "')");
        }
        
        condition.append(dateWhr);
        condition.append(" and (");
       
        delCondition.append(" (");
        for(int j =0;j<forLeaveTime.size();j++){
            condition.append(forLeaveTime.get(j) + ">0");
            delCondition.append(Sql_switcher.isnull((String)forLeaveTime.get(j), "0") + "=0");
            if(j == forLeaveTime.size()-1) {
                condition.append(")");
                delCondition.append(")");
            } else {    
                condition.append(" or ");
                delCondition.append(" and ");
            }
        }
        
        ContentDAO dao = new ContentDAO(this.conn);
        
        /* 
        where Q3303>='2014.12.01' and Q3303<='2014.12.10' 
            and nbase='Usr'
            and exists(select 1 from Q03 where Q03Z0>='2014.12.01' and Q03Z0<='2014.12.10' 
            and (ISNULL(Q0331,0)=0 and ISNULL(Q0333,0)=0 and ISNULL(q0329,0)=0) 
            and a0100 in ( select A0100  from UsrA01  WHERE  a0100 in ( select a0100 from kt_su_dd where nbase='Usr'))
            and q03.nbase=Q33.nbase and Q03.a0000=Q33.A0100 and q03.Q03Z0=Q33.Q3303)
        */
        //清除日明细中加班为零的调休加班记录
        delCondition.append(" and a0100 in ( " + jointable2 + ")");
        try {
            StringBuffer delSQL = new StringBuffer("DELETE FROM q33");
            delSQL.append(" WHERE nbase='" + nbase + "'");
            delSQL.append(" AND Q3303>='" + startDate + "'");
            delSQL.append(" AND Q3303<='" + endDate + "'");
            //zxj 20170525 已经使用过的不能删，因为有可能数据处理异常，加班没正确处理出来
            delSQL.append(" AND ").append(Sql_switcher.isnull("Q3307", "0")).append("=0");
            delSQL.append(" AND EXISTS(SELECT 1 FROM Q03");
            delSQL.append(" WHERE nbase='" + nbase + "'");
            delSQL.append(" AND Q03Z0>='" + startDate + "'");
            delSQL.append(" AND Q03Z0<='" + endDate + "'");
            delSQL.append(" AND " + delCondition.toString());
            delSQL.append(" AND Q03.nbase=Q33.nbase");
            delSQL.append(" AND Q03.A0100=Q33.A0100");
            delSQL.append(" AND Q03.Q03Z0=Q33.Q3303)");
            
            dao.update(delSQL.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        //更新日明细中存在的加班记录
        String sumLenSQL = "";
        for(int i=0;i<forLeaveTime.size();i++){
            String overtimeLen = Sql_switcher.isnull("Q03." + (String)forLeaveTime.get(i), "0");
            
        	Integer itemUnit;
        	if (!this.kqItems.containsKey(array[i])) {
        		itemUnit = Integer.parseInt(getFieldvalue("item_unit", array[i], "item_id"));
        		this.kqItems.put(array[i], itemUnit);
        	} else {
        		itemUnit = (Integer)this.kqItems.get(array[i]);
        	}
            //将分析结果中的值转换为分钟
            switch (itemUnit){
            case 1: //小时
            	sumLenSQL = sumLenSQL + overtimeLen + "*60";
                break;
            case 2: //天
            	sumLenSQL = sumLenSQL + overtimeLen + "*480";
                break;
            case 3: //分钟
            	sumLenSQL = sumLenSQL + overtimeLen;
                break;
            }
            
            if (i<forLeaveTime.size()-1) {
                sumLenSQL += "+";
            }
        }

        String srcTab = "Q03";
        String destTab = "Q33";
        String strJoin = "Q03.nbase=Q33.nbase and Q03.a0100=Q33.a0100 and q03.q03z0=Q33.q3303";
        String strSet = "Q33.Q3305=" + sumLenSQL;
        StringBuffer strDWhere = new StringBuffer();
        strDWhere.append(" Q33.nbase='" + nbase + "'");
        strDWhere.append(" AND Q33.Q3303>='" + startDate + "'");
        strDWhere.append(" AND Q33.Q3303<='" + endDate + "'");
        strDWhere.append(" AND EXISTS(SELECT 1 FROM Q03");
        strDWhere.append(" WHERE nbase='" + nbase + "'");
        strDWhere.append(" AND Q03Z0>='" + startDate + "'");
        strDWhere.append(" AND Q03Z0<='" + endDate + "'");
        strDWhere.append(" AND " + condition.toString());
        strDWhere.append(" AND Q03.nbase=Q33.nbase");
        strDWhere.append(" AND Q03.A0100=Q33.A0100");
        strDWhere.append(" AND Q03.Q03Z0=Q33.Q3303)");
        strDWhere.append(" and Q33.a0100 in ( " + jointable2 + ")");
        
        StringBuffer strSWhere = new StringBuffer();
        strSWhere.append(" Q03.nbase='" + nbase + "'");
        strSWhere.append(" AND Q03.Q03Z0>='" + startDate + "'");
        strSWhere.append(" AND Q03.Q03Z0<='" + endDate + "'");
        strSWhere.append(" AND " + condition.toString());
        strSWhere.append(" and Q03.a0100 in ( " + jointable2 + ")");
        String sql = Sql_switcher.getUpdateSqlTwoTable(destTab, srcTab, strJoin, strSet, strDWhere.toString(), strSWhere.toString());
        try {
			dao.update(sql);
			
			//更新剩余天数
			sql = "UPDATE Q33 SET Q3309=Q3305-" + Sql_switcher.isnull("Q3307", "0")
			    + " WHERE " + strDWhere.toString()
			    + " and Q3305>=" + Sql_switcher.isnull("Q3307", "0");
			dao.update(sql);
			
			sql = "UPDATE Q33 SET Q3309=0"
		    + " WHERE " + strDWhere.toString()
		    + " and Q3305<" + Sql_switcher.isnull("Q3307", "0");
		    dao.update(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
        
        StringBuffer addsql = new StringBuffer();
        addsql.append("select A0100,nbase,Q03Z0,B0110,E0122,E01A1,A0101,"+str+" from (" + jointable1 );
        addsql.append(" INNER JOIN (" + jointable2 + ")B on Q.A0100=B.A0100 AND Q.nbase = '"+nbase+"') A" );
        addsql.append(" where " + condition);
        addsql.append(" and not exists(select 1 from q33");
        addsql.append(" where q33.nbase=A.nbase and q33.a0100=A.a0100 and q33.q3303=A.q03z0)");
        
        ArrayList datalist = new ArrayList();
        
        RowSet rs = null;
        try {
            rs = dao.search(addsql.toString());//检查日明细表Q03
            while(rs.next()){
                //调休加班总分钟
                float overtimeLen = sumOverTimeForLeaveLen(forLeaveTime, array, rs);
                
                String a0100 = rs.getString("A0100");
                String q03Z0 = rs.getString("Q03Z0");
                
                //没有记录则新增
                ArrayList list = new ArrayList();
                IDGenerator idg=new IDGenerator(2,this.conn);
                String value=idg.getId("Q33.Q3301");
                list.add(value);
                list.add(nbase);
                list.add(rs.getString("B0110"));
                list.add(rs.getString("E0122"));
                list.add(rs.getString("E01A1"));
                list.add(rs.getString("A0100"));
                list.add(rs.getString("A0101"));
                list.add(rs.getString("Q03Z0"));
                list.add(Float.valueOf(overtimeLen));
                list.add(Float.valueOf(0));
                list.add(Float.valueOf(overtimeLen));
                datalist.add(list);
            }
            
            if (datalist.size() > 0) {
	            StringBuffer insertSQL = new StringBuffer();
	            insertSQL.append("insert into Q33 (Q3301,nbase,B0110,E0122,E01A1,A0100,A0101,Q3303,Q3305,Q3307,Q3309)");
	            insertSQL.append(" values (?,?,?,?,?,?,?,?,?,?,?)");
	            dao.batchInsert(insertSQL.toString(), datalist);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }     
    }
    
    /**
     * @Title: sumOverTimeForLeaveLen   
     * @Description: 得到某人某天调休加班的和（分钟）   
     * @param @param forLeaveTime  调休加班指标列表
     * @param @param overTimeIds   调休加班类型编码数组
     * @param @param analyseResult 数据处理结果集
     * @param @return 
     * @return int    
     * @throws
     */
    private int sumOverTimeForLeaveLen(ArrayList forLeaveTime, String[] overTimeIds, RowSet analyseResult) {
        float sumLen = 0;
        
        for(int i=0;i<forLeaveTime.size();i++){
            float overtimeLen = 0;
            try {
                overtimeLen = analyseResult.getFloat((String) forLeaveTime.get(i));
            } catch(Exception e) {
                overtimeLen = 0;
            }
            
            if (overtimeLen > 0) {
            	Integer itemUnit;
            	if (!this.kqItems.containsKey(overTimeIds[i])) {
            		itemUnit = Integer.parseInt(getFieldvalue("item_unit", overTimeIds[i], "item_id"));
            		this.kqItems.put(overTimeIds[i], itemUnit);
            	} else {
            		itemUnit = (Integer)this.kqItems.get(overTimeIds[i]);
            	}
                //将分析结果中的值转换为分钟
                switch (itemUnit){
                case 1: //小时
                    sumLen = sumLen + overtimeLen * 60;
                    break;
                case 2: //天
                    sumLen = sumLen + overtimeLen * 8  * 60;
                    break;
                case 3: //分钟
                    sumLen = sumLen + overtimeLen;
                    break;
                }
            }
        }
        
        //四舍五入取整
        if (sumLen > 0) {
            sumLen = new java.math.BigDecimal(sumLen).setScale(0, java.math.RoundingMode.HALF_UP).intValue();
        }
        
        return (int)sumLen;
    }
    
    /**
     * 销调休假时 返换销掉的时长
     * @param vo
     */
    public void returnLeaveTime(RecordVo vo ) throws GeneralException {
        String a0100 = vo.getString("a0100");
        String nbase = vo.getString("nbase");
        StringBuffer sb = new StringBuffer();
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        //int validityTime = Integer.parseInt(KqParam.getInstance().getOVERTIME_FOR_LEAVETIME_LIMIT());
        Date fromDate = null;
        Date toDate = null;
        Object obj1 = vo.getObject("q15z1");
        Object obj3 = vo.getObject("q15z3");
        if (obj1 instanceof Date) 
        {
            fromDate = (Date) obj1;
            toDate = (Date) obj3;
        } else if (obj1 instanceof String) 
        {
            fromDate =  OperateDate.strToDate((String) obj1, "yyyy-MM-dd HH:mm");
            toDate =  OperateDate.strToDate((String) obj3, "yyyy-MM-dd HH:mm");
        }
        //请调休假和返回调休假都统一取从当前日期往前算的范围内的调休加班记录
        //String start_d = OperateDate.dateToStr(OperateDate.addDay(new Date(), 0-validityTime),"yyyy.MM.dd");
        //String end_d = OperateDate.dateToStr(new Date(),"yyyy.MM.dd");
        KqOverTimeForLeaveBo kqOverTimeForLeave = new KqOverTimeForLeaveBo(this.conn, this.userView);
        HashMap period = kqOverTimeForLeave.getEffectivePeriod();
        String start_d = ((String)period.get("from")).replaceAll("-", ".");
        String end_d = ((String)period.get("to")).replaceAll("-", ".");
        
        ArrayList list = new ArrayList();
        list.add(nbase);
        list.add(a0100);
        list.add(start_d);
        list.add(end_d);
        
        try
        {
            // 考勤规则应取改假类自己的规则
            AnnualApply annualApply = new AnnualApply(userView, conn);
            HashMap kqItemHash = annualApply.count_Leave(vo.getString("q1503"));
            kqItemHash.put("item_unit", KqConstant.Unit.HOUR);
            //假期时长扣减规则参数
            float[] holidayRules = null; //annualApply.getHoliday_minus_rule();
            if (KqParam.getInstance().isHoliday(this.conn, vo.getString("b0110"), vo.getString("q1503"))) {
                holidayRules = annualApply.getHoliday_minus_rule();
            }
            
            float timeLen = annualApply.calcLeaveAppTimeLen(nbase, a0100, "", fromDate, toDate, kqItemHash, holidayRules, Integer.MAX_VALUE);
            
            int timescount = (int)(timeLen * 60);
            // 未调休限额小时数
            int maxLimit = Integer.parseInt(KqParam.getInstance().getOVERTIME_FOR_LEAVETIME_MAX_HOUR());
            // 34123 只有报批状态时校验限额，审批时不需校验
        	if(maxLimit>0 && "02".equalsIgnoreCase(vo.getString("q15z5"))) {
        		// 查询现有调休时长
        		sb.append("select sum(Q3309) from Q33 where nbase =? and a0100 =? and Q3303>=? and Q3303<=? ");
        		rs = dao.search(sb.toString(), list);
        		int timesBefore = 0;
        		if(rs.next()){
        			timesBefore = rs.getInt(1);
        		}
        		// 被销假的时长加上现有时长 超过 规定的未调休限额数 不允许销假操作
        		if((timesBefore+timescount)>maxLimit*60) {
        			StringBuffer msg = new StringBuffer("");
        			msg.append("未调休加班限额").append(maxLimit).append("小时。<br/>");
        			msg.append(vo.getString("a0101"));
        			msg.append("请假单（").append(DateUtils.format(fromDate, "yyyy-MM-dd HH:mm")).append("~").append(DateUtils.format(toDate, "yyyy-MM-dd HH:mm")).append("）时长是").append(timeLen).append("小时，<br/>");
        			msg.append("当前未调休加班时长是").append(timesBefore/60).append("小时，");
        			msg.append("合计 ").append(timeLen+timesBefore/60).append("小时，已超出限额要求！");
        			throw new GeneralException(msg.toString());
        		}
        			
        	}
            
            sb.setLength(0);
            sb.append("select * from Q33 where q3307 != 0 and nbase =? and a0100 =? and Q3303>=? and Q3303<=? order by q3303 desc");
            rs = dao.search(sb.toString(), list);
            while(rs.next()){
                int q3307 = rs.getInt("q3307");
                int q3309 = rs.getInt("q3309");
                String q3303 = rs.getString("q3303");
                StringBuffer sb2 = new StringBuffer();
                if(q3307>=timescount){
                    sb2.append("update Q33 set q3307 = "+(q3307-timescount)+",q3309 = "+(timescount+q3309)+" " +
                            "where nbase = '"+nbase+"' and a0100 ='"+a0100+"' and q3303='"+q3303+"'");
                }else{
                    sb2.append("update Q33 set q3307 = 0,q3309 = "+(q3307+q3309)+" " +
                            "where nbase = '"+nbase+"' and a0100 ='"+a0100+"' and q3303='"+q3303+"'");
                }
                dao.update(sb2.toString());
                timescount = timescount - q3307;
                if(timescount <= 0) {
                    break;
                }
            }
        }  catch (GeneralException e) {
            throw e;
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
    }
    
    /**
     * 根据item_id获取考勤规则表 kq_item 里面的某个字段的值
     * @param field
     * @param itemid
     * @return 
     */
    private String getFieldvalue(String selectField,String whereValue,String whereField){
        StringBuffer buf = new StringBuffer();
        buf.append("select " + selectField + " from kq_item where ");
        buf.append(whereField + " = '"+whereValue+"'");
        ContentDAO dao = new ContentDAO(this.conn);
        String fieldvalue = "";
        RowSet rowset = null;
        try
        {
            rowset = dao.search(buf.toString());
            if(rowset.next()){
                fieldvalue = rowset.getString(selectField);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }finally{
           KqUtilsClass.closeDBResource(rowset);
        }
        return fieldvalue;
    }
    
  /**
     * 根据调休假请假时长 更新调休加班明细表Q33
     * @param nbase
     * @param a0100
     * @param appTime
     */
    public boolean upQ33(String nbase,String a0100,int appTime){
        boolean result = false;
        
        KqOverTimeForLeaveBo kqOverTimeForLeave = new KqOverTimeForLeaveBo(conn, this.userView);
        HashMap period = kqOverTimeForLeave.getEffectivePeriod(this.getStartDate());
        String start_d = ((String)period.get("from")).replaceAll("-", ".");
        String end_d = ((String)period.get("to")).replaceAll("-", ".");
        
        StringBuffer sql = new StringBuffer();
        sql.append("select Q3303,Q3309,Q3307 from Q33 ");
        sql.append("where Q3303 >= '" + start_d + "' and Q3303 <= '" + end_d + "'");
        sql.append(" and nbase = '" + nbase + "' and a0100 = '" + a0100 + "'");
        sql.append(" ORDER BY Q3303");
        
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try {
            rs = dao.search(sql.toString());
            while (rs.next()) {
                String date = rs.getString("Q3303");
                String wheresql = " where Q3303 ='"+date+"' and nbase ='"+nbase+"' and a0100='"+a0100+"'";
                
                StringBuffer uptSQL = new StringBuffer();
                int time1 = rs.getInt("Q3309");
                if(time1 >= appTime) {
                    uptSQL.append("update Q33 set Q3307=Q3307+'"+appTime+"',Q3309='"+(time1-appTime)+"'"+wheresql);
                } else {
                    uptSQL.append("update Q33 set Q3307=Q3305,Q3309=0 "+wheresql);
                }
                
                dao.update(uptSQL.toString());
                
                appTime = appTime - time1;
                if(appTime <= 0){
                    result = true;
                    break;
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally{
            KqUtilsClass.closeDBResource(rs);
        }
        return result;
    }

	public Date getStartDate() {
		return startDate;
	}
	
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
}
