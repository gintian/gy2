package com.hjsj.hrms.businessobject.train;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.*;

public class TrainPlanBo {
    
    private Connection conn=null;
    public TrainPlanBo(Connection con)
    {
        this.conn=con;
    }
    
    
    
    
    public String getDataValue(String fielditemid,String operate,String value)
    {
        StringBuffer a_value=new StringBuffer("");      
        try
        {

                if("=".equals(operate))
                {
                    a_value.append("(");
                    a_value.append(Sql_switcher.year(fielditemid)+operate+value.substring(0,4)+" and ");
                    a_value.append(Sql_switcher.month(fielditemid)+operate+value.substring(5,7)+" and ");
                    a_value.append(Sql_switcher.day(fielditemid)+operate+value.substring(8));
                    a_value.append(" ) ");
                }
                else 
                {
                    a_value.append("(");
                    if(">=".equals(operate))
                    {
                        
                        a_value.append(Sql_switcher.year(fielditemid)+">"+value.substring(0,4)+" or ( ");
                        a_value.append(Sql_switcher.year(fielditemid)+"="+value.substring(0,4)+" and "+Sql_switcher.month(fielditemid)+">"+value.substring(5,7)+" ) or ( ");                        
                    }
                    else if("<=".equals(operate))
                    {
                        
                        a_value.append(Sql_switcher.year(fielditemid)+"<"+value.substring(0,4)+" or ( ");
                        a_value.append(Sql_switcher.year(fielditemid)+"="+value.substring(0,4)+" and "+Sql_switcher.month(fielditemid)+"<"+value.substring(5,7)+" ) or ( ");                        
                    }
                    else
                    {
                        
                        a_value.append(Sql_switcher.year(fielditemid)+operate+value.substring(0,4)+" or ( ");
                        a_value.append(Sql_switcher.year(fielditemid)+"="+value.substring(0,4)+" and "+Sql_switcher.month(fielditemid)+operate+value.substring(5,7)+" ) or ( ");
                        
                    }
                    a_value.append(Sql_switcher.year(fielditemid)+"="+value.substring(0,4)+" and "+Sql_switcher.month(fielditemid)+"="+value.substring(5,7)+" and "+Sql_switcher.day(fielditemid)+operate+value.substring(8));
                    a_value.append(") ) ");
                }

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return a_value.toString();
    }
    
    
    
    
    public ArrayList getPlanFieldList(ArrayList fieldList,UserView userView,String planID)
    {
        ArrayList list=new ArrayList();
        ContentDAO dao=new ContentDAO(this.conn);
        RowSet rowSet=null;
        try
        {
            rowSet=dao.search("select * from r25 where r2501='"+planID+"'");
            rowSet.next();
            
            for(int i=0;i<fieldList.size();i++)
            {
                FieldItem item=(FieldItem)fieldList.get(i);
                if("r2508".equalsIgnoreCase(item.getItemid())|| "r2509".equalsIgnoreCase(item.getItemid())|| "r2513".equalsIgnoreCase(item.getItemid()))  //编制日期||状态
                {
                    continue;
                }
                LazyDynaBean abean=new LazyDynaBean();
                abean.set("itemid",item.getItemid());
                abean.set("itemdesc",item.getItemdesc());
                String value="";
                String viewValue="";
                if(rowSet!=null&&!"D".equalsIgnoreCase(item.getItemtype())) {
                    value=rowSet.getString(item.getItemid());
                } else if(rowSet!=null&& "D".equalsIgnoreCase(item.getItemtype()))
                {
                    SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
                    Date d=rowSet.getDate(item.getItemid());
                    Calendar calendar=Calendar.getInstance();
                    calendar.setTime(d);
                    calendar.add(Calendar.MONTH,-1);
                    value=format.format(calendar.getTime());
                }
                
                if("b0110".equalsIgnoreCase(item.getItemid()))
                {
                    if(value!=null&&value.trim().length()>0) {
                        viewValue=AdminCode.getCodeName("UN",userView.getUserOrgId());
                    }
                }
                if("e0122".equalsIgnoreCase(item.getItemid()))
                {
                    if(value!=null&&value.trim().length()>0) {
                        viewValue=AdminCode.getCodeName("UM",value);
                    }
                }
                if("r2505".equalsIgnoreCase(item.getItemid()))  //月份
                {
                    if(value!=null&&value.trim().length()>0) {
                        viewValue=AdminCode.getCodeName("13",value);
                    }
                    
                }
                if("r2504".equalsIgnoreCase(item.getItemid()))  //季度
                {
                    if(value!=null&&value.trim().length()>0) {
                        viewValue=AdminCode.getCodeName("12",value);
                    }
                }
                if("r2512".equalsIgnoreCase(item.getItemid()))  //计划审批方式
                {
                    value="01";
                    viewValue="直批";
                }
                if(value==null) {
                    abean.set("value","");
                } else {
                    abean.set("value",value);
                }
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
    
    
    
    public ArrayList getPlanFieldList(ArrayList fieldList,UserView userView)
    {
        ArrayList list=new ArrayList();
        
        for(int i=0;i<fieldList.size();i++)
        {
            FieldItem item=(FieldItem)fieldList.get(i);
            if("r2508".equalsIgnoreCase(item.getItemid())|| "r2509".equalsIgnoreCase(item.getItemid())|| "r2513".equalsIgnoreCase(item.getItemid()))  //编制日期||状态
            {
                continue;
            }
            LazyDynaBean abean=new LazyDynaBean();
            abean.set("itemid",item.getItemid());
            abean.set("itemdesc",item.getItemdesc());
            String value="";
            String viewValue="";
            if("r2503".equalsIgnoreCase(item.getItemid()))
            {
                Calendar calendar=Calendar.getInstance();
                value=String.valueOf(calendar.get(Calendar.YEAR));
            }
            if("b0110".equalsIgnoreCase(item.getItemid()))
            {
                value=userView.getUserOrgId();
                viewValue=AdminCode.getCodeName("UN",userView.getUserOrgId());
            }
            if("e0122".equalsIgnoreCase(item.getItemid()))
            {
                value=userView.getUserDeptId();
                viewValue=AdminCode.getCodeName("UM",value);
            }
            if("r2505".equalsIgnoreCase(item.getItemid()))  //月份
            {
                Calendar calendar=Calendar.getInstance();
                int month=calendar.get(Calendar.MONTH)+1;
                if(month<10) {
                    value="0"+month;
                } else {
                    value=String.valueOf(month);
                }
                viewValue=AdminCode.getCodeName("13",value);
                
            }
            if("r2504".equalsIgnoreCase(item.getItemid()))  //季度
            {
                Calendar calendar=Calendar.getInstance();
                int month=calendar.get(Calendar.MONTH)+1;
                if(month<4) {
                    value="01";
                } else if(month<7) {
                    value="02";
                } else if(month<10) {
                    value="03";
                } else if(month<13) {
                    value="04";
                }
                viewValue=AdminCode.getCodeName("12",value);
            }
            if("r2512".equalsIgnoreCase(item.getItemid()))  //计划审批方式
            {
                value="01";
                viewValue="直批";
            }
            abean.set("value",value);
            abean.set("viewvalue",viewValue);
            abean.set("decimalwidth",String.valueOf(item.getDecimalwidth()));
            abean.set("itemtype",item.getItemtype());
            abean.set("itemlength",String.valueOf(item.getItemlength()));
            abean.set("codesetid",item.getCodesetid());
            list.add(abean);
        }
        return list;
    }
    
    
    
    // timeFlag      显示时间条件  1：全部   2：本年度   3：本季度  4：本月份 5.某时间段
    public String getDataConditionSql(String timeFlag,String startTime,String endTime)
    {
        StringBuffer conditionStr=new StringBuffer("");
        GregorianCalendar d = new GregorianCalendar();
        if("2".equals(timeFlag))
        {
            conditionStr.append(" and R3119 ='"+d.get(Calendar.YEAR)+"'");
            
        }
        else if("3".equals(timeFlag))
        {
            int year = d.get(Calendar.YEAR);
            int month=d.get(Calendar.MONTH)+1;
            if(month>=1&&month<=3)
            {
                conditionStr.append(" and R3119 ='"+year+"' and R3120='01'");
            }
            else if(month>3&&month<=6)
            {
                conditionStr.append(" and R3119 ='"+year+"' and R3120='02'");
            }
            else if(month>6&&month<=9)
            {
                conditionStr.append(" and R3119 ='"+year+"' and R3120='03'");
            }
            else
            {
                conditionStr.append(" and R3119 ='"+year+"' and R3120='04'");
            }
        }
        else if("4".equals(timeFlag))
        {
            int year = d.get(Calendar.YEAR);
            int month=d.get(Calendar.MONTH)+1;
            String imonth = String.valueOf(month);
            if(month < 10){
                imonth = "0"+imonth;
            }
            conditionStr.append(" and R3119 ='"+year+"' and R3121 ='"+imonth+"'");
        }
        else if("5".equals(timeFlag)){
            if(startTime!=null&&startTime.length()>2){
                String[] date=startTime.split("-");
                if(date[1].length() == 1){//判断月份长度，当月份输入为“1”、“2”的情况转换为“01”、“02”
                    date[1]="0"+date[1];
                }
                if(Sql_switcher.searchDbServer() == Constant.ORACEL){
                    conditionStr.append(" and concat(r3119,nvl(r3121,"+date[1]+"))>="+date[0]+date[1]);
                }else{
                    conditionStr.append(" and (r3119+isnull(r3121,"+date[1]+"))>="+date[0]+date[1]);
                }
            }
            if(endTime!=null&&endTime.length()>2){
                String[] date=endTime.split("-");
                if(date[1].length() == 1){//判断月份长度，当月份输入为“1”、“2”的情况转换为“01”、“02”
                    date[1]="0"+date[1];
                }
                if(Sql_switcher.searchDbServer() == Constant.ORACEL){
                    conditionStr.append(" and concat(r3119,nvl(r3121,"+date[1]+"))<="+date[0]+date[1]);
                }else{
                    conditionStr.append(" and (r3119+isnull(r3121,"+date[1]+"))<="+date[0]+date[1]);
                }
            }
        }
        return  conditionStr.toString();
    }
    
    
    /**
     * 单据状态列表
     * @return
     */
    /*public ArrayList getPlanStateList()
    {
        ArrayList list=new ArrayList();
        CommonData data1=new CommonData("0","全部");
        CommonData data2=new CommonData("01","起草");
        CommonData data3=new CommonData("03","已批");
        CommonData data4=new CommonData("05","执行中");
        CommonData data5=new CommonData("06","结束");
        list.add(data1);
        list.add(data2);
        list.add(data3);
        list.add(data4);
        list.add(data5);
        return list;
    }
    */
    
    
    /**
     * 时间条件列表
     * @return
     */
    /*public ArrayList getTimeConditionList()
    {
        ArrayList list=new ArrayList();
        CommonData data1=new CommonData("1","全部");
        CommonData data2=new CommonData("2","本年度");
        CommonData data3=new CommonData("3","本季度");
        CommonData data4=new CommonData("4","本月份");
        CommonData data5=new CommonData("5","某时间段");
        list.add(data1);
        list.add(data2);
        list.add(data3);
        list.add(data4);
        list.add(data5);
        return list;
    }*/
    
    
    //得到已批计划列表
    public ArrayList getRatifyTrainPlanList()
    {
        ArrayList list=new ArrayList();
        ContentDAO dao=new ContentDAO(this.conn);
        try
        {
            RowSet frowset=null;
            frowset=dao.search("select * from r25 where r25.r2509='03' order by r2508 desc");
            CommonData data=null;
            while(frowset.next())
            {
                data=new CommonData(SafeCode.encode(PubFunc.encrypt(frowset.getString("r2501"))),frowset.getString("r2502"));
                list.add(data);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return list;
        
    }
    
    
    //得到培训计划列表
    public ArrayList getTrainPlanList(UserView userView)
    {
        ArrayList list=new ArrayList();
//      CommonData data1=new CommonData("0","");
//      list.add(data1);
        CommonData data2=new CommonData(SafeCode.encode(PubFunc.encrypt("-1")),"全部");
        list.add(data2);
        ContentDAO dao=new ContentDAO(this.conn);
        try
        {
            TrainCourseBo tp = new TrainCourseBo(userView);
            String codes = tp.getUnitIdByBusi();
            RowSet frowset=null;
            if(!"".equals(codes)&&!userView.isSuper_admin()&&codes.indexOf("UN`")==-1 ){
                StringBuffer sql = new StringBuffer();
                sql.append("select * from r25,(select * from codeitem where codesetid='23') codeitem ");
                sql.append("where r25.r2509=codeitem.codeitemid ");
                String[] tmp = codes.split("`");
                StringBuffer tmpstr=new StringBuffer();
                for(int i=0;i<tmp.length;i++){
                    String code = tmp[i];
                    if(i>0) {
                        tmpstr.append(" or ");
                    }
                    if ("UN".equalsIgnoreCase(code.substring(0, 2))) {
                        tmpstr.append("r25.B0110 like '" + code.substring(2, code.length()) + "%'");
                    }
                    if ("UM".equalsIgnoreCase(code.substring(0, 2))) {
                        tmpstr.append("r25.E0122 like '" + code.substring(2, code.length()) + "%'");
                    }
                }
                if(tmpstr!=null&&tmpstr.length()>0) {
                    sql.append("and ("+tmpstr+") ");
                }
                sql.append("order by r2508 desc");
                frowset=dao.search(sql.toString());
            }else{
                frowset=dao.search("select * from r25,(select * from codeitem where codesetid='23') codeitem where r25.r2509=codeitem.codeitemid order by r2508 desc");
            }
            CommonData data=null;
            while(frowset.next())
            {
                String temp=frowset.getString("codeitemdesc");
                data=new CommonData(SafeCode.encode(PubFunc.encrypt(frowset.getString("r2501"))),frowset.getString("r2502")+"("+temp+")");
                list.add(data);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return list;
    }
    
    
    
    /**
     * 
     * @param codeid
     * @param codeset
     * @param timeFlag      显示时间条件  1：全部   2：本年度   3：本季度  4：本月份 5.某时间段
     * @param startTime
     * @param endTime
     * @param stateFlag     显示状态条件 0：所有状态  01：起草状态  03:已批状态  05:执行中状态  06：结束状态
     * @param model         1:计划制定   2：计划审核
     * @param trainPlanID   计划id
     * @return
     */
    public String getPlanListSql(UserView userView,String codeid,String codeset,
            String timeFlag,String startTime,String endTime,String stateFlag,
            String model,String trainPlanID,String extendSql,String orderSql)
    {
        StringBuffer sql=new StringBuffer("select ");
        sql.append("r31.*  from r31 ");     
        StringBuffer whl=new StringBuffer("");

        if("2".equals(model)&& "0".equals(trainPlanID)) {
            whl.append(" and ( r3125 is null or r3125='' ) ");
        } else if("2".equals(model)&&!"0".equals(trainPlanID)&&!"-1".equals(trainPlanID)) {
            whl.append(" and r3125='"+trainPlanID+"'");
        }
        
        if(codeset!=null&&codeid!=null&&!"".equals(codeset)&&!"".equals(codeid))
        {
            if("UN".equals(codeset))
            {
                whl.append(" and b0110 like '"+codeid+"%' ");
            }
            else if("UM".equals(codeset))
            {
                whl.append(" and e0122 like '"+codeid+"%' ");
            }
        }
        if(!"1".equals(timeFlag))
        {
            whl.append(" "+getDataConditionSql(timeFlag,startTime,endTime));
        }
        if(!"00".equals(stateFlag))
        {           
                whl.append(" and r3127='"+stateFlag+"'");           
        }
        if("2".equals(model)) {
            whl.append(" and r3127<>'01' and r3127<>'08' ");
        } else
        {
            if(userView.hasTheFunction("090403")&&!userView.hasTheFunction("090404"))
            {
                whl.append(" and r3127<>'01' ");
            }
            
        }

        if(whl.toString().trim().length()>0)
        {
            sql.append(" where "+whl.substring(5));
        }
        if(extendSql!=null&&extendSql.trim().length()>0){
            if(sql.indexOf("where")!=-1){
                sql.append(" and ( "+extendSql+" )");
            }else{
                if(sql.indexOf("WHERE")!=-1){
                    sql.append(" and ( "+extendSql+" )");
                }else{
                    sql.append(" where ( "+extendSql+" )");
                }
            }
        }
        if(orderSql!=null&&orderSql.trim().length()>0) {
            sql.append(" "+orderSql);
        } else {
            sql.append(" order by b0110,e0122,r3127,r3101");
        }
        return sql.toString();
    }
    
    /**
     * 
     * @param codeid
     * @param codeset
     * @param timeFlag      显示时间条件  1：全部   2：本年度   3：本季度  4：本月份 5.某时间段
     * @param startTime
     * @param endTime
     * @param stateFlag     显示状态条件 0：所有状态  01：起草状态  03:已批状态  05:执行中状态  06：结束状态
     * @param model         1:计划制定   2：计划审核
     * @param trainPlanID   计划id
     * @return
     */
    public String getPlanListSql1(UserView userView,String codeid,String codeset,
            String timeFlag,String startTime,String endTime,String stateFlag,
            String model,String trainPlanID,String extendSql,String orderSql,ArrayList fieldlist) {
        
        StringBuffer sql=new StringBuffer("select ");
        try {
            for(int i=0;i<fieldlist.size();i++){
                FieldItem item=(FieldItem)fieldlist.get(i);
                sql.append(item.getItemid()+",");
            }
            sql.append("(select R2502 from r25 where r2501=r31.r3125) as trainplan");
            sql.append("  from r31 ");      
            StringBuffer whl=new StringBuffer("");
            
            if("2".equals(model)&& "0".equals(trainPlanID)) {
                whl.append(" and ( r3125 is null or r3125='' ) ");
            } else if("2".equals(model)&&!"0".equals(trainPlanID)&&!"-1".equals(trainPlanID)) {
                whl.append(" and r3125='"+trainPlanID+"'");
            }
            
            //PerformanceImplementBo bo = new PerformanceImplementBo(this.conn);
            //whl.append(bo.getPrivWhere(userView));        
            
            if(codeset!=null&&codeid!=null&&!"".equals(codeset)&&!"".equals(codeid))
            {
                if("UN".equals(codeset))
                {
                    whl.append(" and b0110 like '"+codeid+"%' ");
                }
                else if("UM".equals(codeset))
                {
                    whl.append(" and e0122 like '"+codeid+"%' ");
                }
            }
            if(!"1".equals(timeFlag))
            {
                whl.append(" "+getDataConditionSql(timeFlag,startTime,endTime));
            }
            if(!"00".equals(stateFlag))
            {           
                whl.append(" and r3127='"+stateFlag+"'");           
            }
            if("2".equals(model)) {
                whl.append(" and r3127<>'01' and r3127<>'08' and r3127<>'07'");
            }
            
            TrainCourseBo bo= new TrainCourseBo(userView);
            String code = bo.getUnitIdByBusi();
            if(!userView.isSuper_admin()&&code.length()>2&&code.indexOf("UN`")==-1){
                whl.append(" and (");
                String temp[] = code.split("`");
                for (int i = 0; i < temp.length; i++) {
                    if(i>0) {
                        whl.append(" or");
                    }
                    if ("UN".equalsIgnoreCase(temp[i].substring(0, 2))) {
                        whl.append(" b0110 like '" + temp[i].substring(2)
                                + "%'");
                    } else if ("UM".equalsIgnoreCase(temp[i].substring(0, 2))) {
                        whl.append(" e0122 like '" + temp[i].substring(2)
                                + "%'");
                    }
                }
                whl.append(")");
            }
            
            if(whl.toString().trim().length()>0)
            {
                sql.append(" where "+whl.substring(5));
            }
            if(extendSql!=null&&extendSql.trim().length()>0){
                if(whl.toString().trim().length()>0){
                    sql.append(" and ( "+extendSql+" )");
                }else{
                    sql.append(" where ( "+extendSql+" )");
                }
            }
            if(orderSql!=null&&orderSql.trim().length()>0) {
                sql.append(" "+orderSql);
            } else {
                sql.append(" order by r3101 desc,b0110,e0122,r3127");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sql.toString();
    }
    
    
    
    /**
     * 根据操作对象得到所属的部门 和 单位
     * @param codeid  
     * @param codeSet
     * @param model    1:计划制定   2：计划审核
     * @param userView
     * @return
     */
    public HashMap getUnit_Depart(String codeid,String codeSet,String model,UserView userView)
    {

        HashMap map=new HashMap();
        codeSet=codeSet!=null&&codeSet.trim().length()>0?codeSet:"";
        ContentDAO dao=new ContentDAO(this.conn);
        try
        {
            RowSet frowset=null;
            if("1".equals(model))
            {
                boolean flag=false;
                if(!userView.isAdmin()&&!"1".equals(userView.getGroupId()))
                {
                    
                    String dbname=userView.getDbname();
                    if(dbname!=null&&!"".equals(dbname))
                    {
                        frowset=dao.search("select * from "+dbname+"A01 where a0100='"+userView.getUserId()+"'");
                        if(frowset.next())
                        {
                            flag=true;
                            map.put("b0110",frowset.getString("b0110"));
                            map.put("e0122",frowset.getString("e0122"));
                        }
                    }
                }
                
                if(!flag)
                {
                    map.put("b0110","");
                    map.put("e0122","");
                }
            }
            else
            {
                if(codeid!=null&&!"".equals(codeid))
                {
                    if("UN".equals(codeSet))
                    {
                        map.put("b0110",codeid);
                        map.put("e0122","");
                    }
                    else if("UM".equals(codeSet))
                    {
                        
                        String a_codeid=codeid;
                        
                        boolean isNotUnit=true;
                        while(isNotUnit)
                        {
                            frowset=dao.search("select * from  organization where codeitemid = (select parentid from organization where codeitemid='"+a_codeid+"' and codeitemid<>parentid)");
                            if(frowset.next())
                            {
                                String codeitemid=frowset.getString("codeitemid");
                                String codesetid=frowset.getString("codesetid");
                                if("UN".equals(codesetid))
                                {
                                    isNotUnit=false;
                                    map.put("b0110",codeitemid);
                                    map.put("e0122",codeid);
                                    
                                }
                                else {
                                    a_codeid=codeitemid;
                                }
                            }
                        }
                    }
                }
                else
                {
                    map.put("b0110","");
                    map.put("e0122","");
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return map;
    }
    /**
     * 检测是否是权限范围内的培训计划
     * @param classid 培训计划编号
     * @param userView 当前用户
     * @return
     */
    public boolean checkPlanPiv(String id, UserView userView) {
        boolean flag = false;
        if (userView.isSuper_admin()) {
            flag = true;
        } else {
            RowSet rs = null;
            try {
                String sql = "select 1 from r25 where r2501='" + id + "' ";
                String whereStr = TrainCourseBo.getUnitIdByBusiStrWhere(userView);
                sql += whereStr.replaceFirst("where", "and");
                ContentDAO dao = new ContentDAO(conn);
                rs = dao.search(sql);
                if (rs.next()) {
                    flag = true;
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (rs != null) {
                        rs.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return flag;
    }
    
}

