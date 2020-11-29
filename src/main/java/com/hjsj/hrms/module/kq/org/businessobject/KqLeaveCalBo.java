package com.hjsj.hrms.module.kq.org.businessobject;

import com.hjsj.hrms.businessobject.kq.interfaces.KqConstant;
import com.hjsj.hrms.businessobject.kq.kqself.AnnualApply;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.businessobject.kq.register.KqReportInit;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.valueobject.UserView;
import com.ibm.icu.text.SimpleDateFormat;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;


public class KqLeaveCalBo {
    private Connection frameconn;
    private UserView userView;
    public KqLeaveCalBo(Connection frameconn, UserView userView) {
        this.frameconn = frameconn;
        this.userView = userView;
    }
    //考勤规则中定义的各种请假类别
    public ArrayList<HashMap>kqLeaveType(String leaveTypes) {
        ArrayList<HashMap> list = new ArrayList<HashMap>();
        RowSet rs = null;	
        try {           		
            ContentDAO dao = new ContentDAO(this.frameconn);
            leaveTypes = leaveTypes.replace(",", "','");
            StringBuffer sql = new StringBuffer("");
            sql.append("SELECT item_id,item_name,item_color FROM  kq_item WHERE sdata_src = 'Q15'");            
                rs = dao.search(sql.toString());
                while (rs.next()) {                    
                    HashMap map = new HashMap();
                    map.put("id", rs.getString("item_id"));
                    String  item_name = rs.getString("item_name");
                    map.put("name", item_name);
                    map.put("color",KqReportInit.getColor(rs.getString("item_color")));
                    list.add(map);
                }              				    
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            PubFunc.closeDbObj(rs);
        }     
        return list;
    }

    //销假与请假进行时间段合并，返回最终有效时间范围
    public ArrayList<HashMap> kqLeaveInfo(String spid, String leaveTypes,String scope, String month) {
        ArrayList<HashMap> list = new ArrayList<HashMap>();
        ArrayList<HashMap> newList = new ArrayList<HashMap>();
        RowSet rs = null;
        ContentDAO dao = new ContentDAO(this.frameconn);
        String nbase = this.userView.getDbname();
        String a0100 = this.userView.getA0100();
        String user_id = nbase + a0100;
        String object_id = user_id;
        String dateh = month;
        try {        
            StringBuffer sqlId = new StringBuffer("");           
            //包过下属或下属的下属
            sqlId.append(" SELECT object_id FROM t_wf_mainbody");
            sqlId.append(" WHERE relation_id = '").append(spid);
            sqlId.append(" ' AND mainbody_id = '").append(user_id).append("' AND sp_grade = 9");
            if ("2".equalsIgnoreCase(scope)) {
                sqlId.append(" UNION ALL");
                sqlId.append(" SELECT object_id FROM t_wf_mainbody");
                sqlId.append(" WHERE mainbody_id IN (select object_id from t_wf_mainbody");
                sqlId.append(" WHERE relation_id = '").append(spid);
                sqlId.append("'  AND mainbody_id = '").append(user_id).append("' AND sp_grade = 9");
                sqlId.append("  ) AND  sp_grade = 9   AND relation_id = '").append(spid).append("'");
            }
            rs = dao.search(sqlId.toString());
            while (rs.next()) {
            	nbase += ",";
            	a0100 += ",";
                nbase +=rs.getString("object_id").substring(0, 3);
                a0100 +=rs.getString("object_id").substring(3);                             
            }
            nbase = nbase.replace(",", "','");
            a0100 = a0100.replace(",", "','");
            leaveTypes = leaveTypes.replace(",", "','");
            
            StringBuffer sql = new StringBuffer("");
            //请假,销假单
            sql.append(" SELECT * FROM  Q15");
            sql.append(" WHERE  Q15z5<>'01' AND Q15z5<>'07'AND Q15z5<>'10'");
            sql.append(" AND  ").append(Sql_switcher.dateToChar("Q15Z3","yyyy-MM") ).append(" >='").append(dateh).append("'");
            sql.append(" AND  ").append(Sql_switcher.dateToChar("Q15Z1","yyyy-MM") ).append(" <='").append(dateh).append("'");
            sql.append(" AND nbase IN ('").append(nbase).append("')");
            sql.append(" AND a0100 IN ('").append(a0100).append("')");
            if (!"initialization".equals(leaveTypes)) {
                sql.append(" AND Q1503 IN ('").append(leaveTypes).append("')");
            }
            rs = dao.search(sql.toString());
            while (rs.next()) {
                HashMap map = new HashMap();
                String  reason = rs.getString("Q1507");     
                String  a0101 = rs.getString("a0101");
                map.put("id", rs.getString("Q1501"));
                map.put("Q15Z5", rs.getString("Q15Z5"));
                map.put("Q1517", rs.getString("Q1517"));
                map.put("Q1519", rs.getString("Q1519"));          
                map.put("nbase", rs.getString("nbase"));
                map.put("a0100", rs.getString("a0100"));
                map.put("a0101", a0101);
                map.put("b0110", rs.getString("b0110"));
                map.put("e01A1", rs.getString("e01A1"));
                map.put("type", rs.getString("Q1503"));               
                map.put("typeName",  AdminCode.getCodeName("27", rs.getString("Q1503")));
                                
                if(existQ15AA()){
                	 map.put("timeLen", rs.getString("Q15AA"));
                     map.put("unit", "天");                                         
                }
                
                if(rs.getTimestamp("Q1505")==null){
                	map.put("applyTime", "");
                }else{
                	map.put("applyTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(rs.getTimestamp("Q1505")));
                }                
                map.put("beginTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(rs.getTimestamp("Q15Z1")));                           
                map.put("endTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(rs.getTimestamp("Q15Z3")));
                if(StringUtils.isEmpty(reason)){
                	map.put("reason", "");
                }else{
                    map.put("reason", removeInValidJsonChar(reason));
                }
                list.add(map);
            }

            for (int i = 0; i < list.size(); i++) {
                String holiday = "1";              
                String Q1501 = (String) ((HashMap) list.get(i)).get("id");
                String Q15z1 = (String) ((HashMap) list.get(i)).get("beginTime");
                String Q15z3 = (String) ((HashMap) list.get(i)).get("endTime");
                String iQ1519 = (String) ((HashMap) list.get(i)).get("Q1519");
                // 33656 linbz 日历中不需显示销假单的数据
                if(StringUtils.isNotEmpty(iQ1519))
                	continue;
                for (int j = 0; j < list.size(); j++) {
                	String Q1519 = (String) ((HashMap) list.get(j)).get("Q1519");
                    String desQ15z1 = (String) ((HashMap) list.get(j)).get("beginTime");
                    String desQ15z3 = (String) ((HashMap) list.get(j)).get("endTime");
                    HashMap hosMap = new HashMap();
                    HashMap desMap = new HashMap();
                    if (Q1501.equals(Q1519)) {
                        holiday = "2";
                        hosMap = list.get(i);                 
                        int res = Q15z1.compareTo(desQ15z1);
                        int resDes = desQ15z3.compareTo(Q15z3);
                        if ((res < 0) && (resDes < 0)) {
                            desMap = (HashMap) hosMap.clone();
                            hosMap.put("beginTime", desQ15z3);
                            this.getList(hosMap,newList);
                            desMap.put("endTime", desQ15z1);                           
                          	String newId = (String)((HashMap) list.get(i)).get("id");
                         	newId+= "1";
                         	desMap.put("id", newId);
                            this.getList(desMap,newList);
                            continue;
                        } else if ((resDes == 0) && (res == 0)) {
                            continue;
                        } else if (resDes == 0) {
                            hosMap.put("endTime", desQ15z1);
                            this.getList(hosMap,newList);                                                      
                            continue;
                        } else if (res == 0) {
                            hosMap.put("beginTime", desQ15z3);
                            this.getList(hosMap,newList); 
                            continue;
                        }
                    }
                }
                if ("1".equals(holiday)) {
                	this.getList(list.get(i),newList);
                }		
				
				Collections.sort(newList, new Comparator<HashMap>() {
					@Override
                    public int compare(HashMap map1, HashMap map2) {
						// name1是从你list里面拿出来的一个				 
						Float name1=Float.valueOf((String) map1.get("timeLen")); 						
					    // name1是从你list里面拿出来的第二个name
						Float name2=Float.valueOf((String) map2.get("timeLen")); 					
						if(name2.compareTo(name1)==0){
							String name3 = (String) map1.get("beginTime");
							String name4 = (String) map2.get("beginTime"); 
							return name3.compareTo(name4);
						}
						return name2.compareTo(name1);	
					}
				});		
            }
        } catch (SQLException e) {      
            e.printStackTrace();
        }finally {
            PubFunc.closeDbObj(rs);
        } 
        return newList;            
    }
    
    private String removeInValidJsonChar(String str) {
        if (StringUtils.isEmpty(str))
            return str;
        
        return str.replace("'", "").replace("\"", "").replace("\r", "")
                .replace("\n", "").replace("\b", "")
                .replace("\t", "").replace("\f", "");
    }
    
    private boolean existQ15AA() {
    	FieldItem item = DataDictionary.getFieldItem("Q15AA", "Q15");
    	return item != null 
    			&& "1".equals(item.getUseflag()) 
    			&& "N".equals(item.getItemtype());
    }
    
    //获取请假天数和单位
    private void getList(HashMap hosMap, ArrayList < HashMap > newList) {
        String newType = (String)((HashMap) hosMap).get("type");
        String newB0110 = (String)((HashMap) hosMap).get("b0110");
        String newNbase = (String)((HashMap) hosMap).get("nbase");
        String newA0100 = (String)((HashMap) hosMap).get("a0100");
        String newQ15z1 = (String)((HashMap) hosMap).get("beginTime");
        String newQ15z3 = (String)((HashMap) hosMap).get("endTime");
        DbWizard dbWriter = new DbWizard(frameconn);
        hosMap.put("nbase", PubFunc.encrypt(newNbase));	
        hosMap.put("a0100", PubFunc.encrypt(newA0100)); 
        if(existQ15AA()){    	            
             newList.add(hosMap);
        }else{
        	 ArrayList listUnit = this.getDate(newType, newQ15z1, newQ15z3, newB0110, newNbase, newA0100);            
             hosMap.put("timeLen", listUnit.get(0));
             hosMap.put("unit", listUnit.get(1));
             newList.add(hosMap);
        }     
    }
     
    private ArrayList getDate(String appType, String beginTime,
        String endTime, String b0110, String nbase, String A0100) {
        try {
            AnnualApply annualApply = new AnnualApply(userView, frameconn);
            ArrayList listUnit = new ArrayList();
            float[] holidayRule = annualApply.getHoliday_minus_rule();     
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");           
            Date newBeginTime = format.parse(beginTime);
            Date newEndTime = format.parse(endTime);
            HashMap kqItem_hash = annualApply.count_Leave(appType);
            String appDesc = (String) kqItem_hash.get("item_name");
            kqItem_hash.put("item_unit","02");
            String unit = (String) kqItem_hash.get("item_unit");        
            String unitDesc = "";
            if (KqConstant.Unit.DAY.equals(unit)) {
                unitDesc = KqConstant.Unit.DAY_DESC;
            } else if (KqConstant.Unit.HOUR.equals(unit)) {
                unitDesc = KqConstant.Unit.HOUR_DESC;
            } else if (KqConstant.Unit.MINUTE.equals(unit)) {
                unitDesc = KqConstant.Unit.MINUTE_DESC;
            } else {
                unitDesc = KqConstant.Unit.TIMES_DESC;
            }
            //年假需要按规则计算申请假期时长
            float[] factHolidayRule = null;
            if (KqParam.getInstance().isHoliday(frameconn, b0110, appType)) {
                factHolidayRule = holidayRule;
            }

            float timeCount = annualApply.calcLeaveAppTimeLen(nbase, A0100,
                    b0110, newBeginTime, newEndTime, kqItem_hash,
                    factHolidayRule, Integer.MAX_VALUE);
            String timeLen = Float.toString(timeCount);          
            unit = unitDesc;            
            listUnit.add(timeLen);
            listUnit.add(unit);
            return listUnit;          
        } catch (Exception e) {
            e.printStackTrace();
        }
		return null;        
    }  
}
