package com.hjsj.hrms.transaction.mobileapp.kq.checkin;

import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.transaction.mobileapp.kq.util.Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.log4j.Category;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 
 * <p>Title: MobileCheckingAttendanceBo </p>
 * <p>Description: 移动考勤业务类</p>
 * <p>Company: hjsj</p>
 * <p>create time  2013-10-23 下午05:13:32</p>
 * @author tiany
 * @version 1.0
 */
public class CheckInMainBo {
    private Connection conn;
    private UserView userView;
    private String table_name = "kq_originality_data";
    private static Category log = Category.getInstance(CheckInMainBo.class.getName());
    
    public CheckInMainBo(){
        
    }
    public CheckInMainBo( Connection conn) {
        this.conn = conn;
    }
    public CheckInMainBo(UserView userView, Connection conn) {
        this.userView = userView;
        this.conn = conn;
    }
    
    /**
     * @param latitude 
     * @param longitude 
     * @throws GeneralException 
     * @Title: CheckInSave   
     * @Description:移动签到保存签到数据入库  
     * @param @return  
     * @return boolean   保存插入数据成功否 
     * @throws
     */
    public String CheckInSave(String location,String oper_cause, String longitude, String latitude,String isCommon,String phoneBindFlag)  {
        String message = "";
        try {
            String a0100 = userView.getA0100();
            String nbase = userView.getDbname();
            String cardno = getKqCard(nbase,a0100);
            java.util.Date date = new Date();
            String work_date = new SimpleDateFormat("yyyy.MM.dd").format(date);
            String work_time = new SimpleDateFormat("HH:mm").format(date);
            String where = "a0100='" + userView.getA0100() 
                       + "' AND nbase='" +  nbase
                       + "' AND work_date='" + work_date 
                       + "' AND work_time='" + work_time + "'";
            if (!isRecordExist(this.table_name, where)){
                StringBuffer sql = new StringBuffer();
                sql.append("insert into ");
                sql.append(this.table_name);
                sql.append("(a0100,nbase,card_no,work_date,work_time,a0101,b0110,e0122,e01a1,location");
                sql.append(",inout_flag,oper_cause,oper_user,oper_time,oper_mach,sp_flag,datafrom,longitude,latitude,iscommon)");
                sql.append(" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                ArrayList list = new ArrayList();
                list.add(a0100);
                list.add(nbase);
                list.add(cardno);//卡号
                list.add(work_date);
                list.add(work_time);
                list.add(userView.getUserFullName());
                list.add(userView.getUserOrgId());
                list.add(userView.getUserDeptId());
                list.add(userView.getUserPosId());
                list.add(location);//签到点
                list.add("0");//进出标志 =0 不限；=1 进；=-1出
                list.add(oper_cause);//补刷卡原因(移动平台为打卡说明信息)
                list.add(null);//oper_user补刷卡操作员
                list.add(null);//oper_time补卡操作时间
                list.add(null);//oper_mach补卡操作机器
                if("0".equals(isCommon)){
                	list.add("02");//已报批（关联系统代码23审批状态）非常规
                }else{
                	list.add("03");//已批（关联系统代码23审批状态）常规
                }
                list.add("2");//数据来源=0 原始刷卡；=1 补刷 ；=2为移动签到
                //经纬度
                list.add(longitude);
                list.add(latitude);
                list.add(isCommon);
                ContentDAO dao = new ContentDAO(this.conn);
                dao.insert(sql.toString(), list);
                if(phoneBindFlag!=null){
                	 String onPhoneBind=SystemConfig.getPropertyValue("m_kq_phone_bind");
                	 RecordVo vo=new RecordVo(nbase+"A01");
                	if(vo.hasAttribute(onPhoneBind.toLowerCase())){
            			vo.setString("a0100",a0100);
            			vo.setString(onPhoneBind.toLowerCase(),phoneBindFlag);
            			dao.updateValueObject(vo);
                	}
                }    
            }else{
                message=ResourceFactory.getProperty("kq.netsign.error.notrepeatsign") ;  
            }
        } catch(Exception e){
			e.printStackTrace();
			String errorMsg = e.toString();
			int index_i = errorMsg.indexOf("description:");
			message = errorMsg.substring(index_i + 12);
        }
        return message;
    }
    /**
     * @throws GeneralException 
     * 
     * @Title: getCheckInfo   
     * @Description:  //获取签到检查信息（签到前查询签到点和签到说明的库内长度,签到点和范围控制，手机绑定标示等)
     * @param  
     * @return void    
     * @throws SQLException 
     * @throws
     */
    public Map getCheckInfo() throws GeneralException, SQLException{
       Map map = new HashMap();
       RecordVo recordVo  = new RecordVo(this.table_name);
       String locationLen = (String)recordVo.getAttrLens().get("location");//签到点
       String checkInDescLen = (String)recordVo.getAttrLens().get("oper_cause");//签到说明
       //tiany 添加签到前查询手机号码 用于签到绑定用户手机号码
/*		ArrayList list = new ArrayList();
		list.add(userView.getDbname() + "`" + userView.getA0100());
		String photoUrl = "";	*/
//		ArrayList contactsOrderList = new ArrayList();// 用于记录后台配置的指标顺序（无用）
//		List oneContactList = new ContactsBo(conn, userView).getContactsList(list, contactsOrderList, photoUrl);
//		if (oneContactList != null && !oneContactList.isEmpty()) {
//			HashMap contactMap = (HashMap) oneContactList.get(0);
//			mobiles = (String) contactMap.get("mobile");
//		}
		//手机绑定标示
       String a0100 = userView.getA0100();
       String nbase = userView.getDbname();
     //绑定唯一标示
       String unique = "";
       /**system 文件下是否启动了绑定手机签到 */
	   String onPhoneBind=SystemConfig.getPropertyValue("m_kq_phone_bind");//是否开启绑定手机功能标示
	   /** 签到已经绑定手机标示 */
	   String phoneBindFlag = "";//用户是否已经绑定过手机标示 默认未绑定
	   if(onPhoneBind!=null&&onPhoneBind.length()!=0)
	   {
		   String uniqueitem = "";
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(conn);
			String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","name");
			FieldItem item = DataDictionary.getFieldItem(onlyname);
			if (item != null) {
				uniqueitem = item.getItemid();	
			}
			RecordVo vo=new RecordVo(nbase+"A01");
			vo.setString("a0100",a0100);
			ContentDAO dao = new ContentDAO(this.conn);
			vo=dao.findByPrimaryKey(vo);
			if(uniqueitem!=null&&uniqueitem.length()!=0){
				unique = vo.getString(uniqueitem.toLowerCase());
			}
			String phoneBindField =onPhoneBind.toLowerCase();
			log.error("设置的绑定指标是否构库："+vo.hasAttribute(phoneBindField));
			System.out.println("设置的绑定指标是否构库："+vo.hasAttribute(phoneBindField));
			if(vo.hasAttribute(phoneBindField)){
				phoneBindFlag = vo.getString(phoneBindField);
				if(phoneBindFlag==null||phoneBindFlag.length()==0){
					phoneBindFlag = "2";// 首次绑定,2未绑定，1绑定
				} 
			}else{
				phoneBindFlag = "";// 空字符窜代表后台配置指定指标代码错误
			}
			onPhoneBind = "true";
	   } else{
		   onPhoneBind = "false"; 
	   }
	   
	   //签到点和范围
	   Map checkInAddrInfo = getCheckInPointsInfo(a0100,nbase);
	   if(!checkInAddrInfo.isEmpty()){
		   map.putAll(checkInAddrInfo);
	   }
	   String nearPoiNum=SystemConfig.getPropertyValue("m_kq_nearPoiNum");//自动锁定考勤点时，最少符合考勤范围周边点个数 默认为3
	   if(nearPoiNum!=null&&nearPoiNum.trim().length()!=0){
		   try
		   {
			   Integer.parseInt(nearPoiNum);
			   map.put("nearPoiNum",nearPoiNum.trim()); 
		   }catch (Exception e) {
			   System.out.println("m_kq_nearPoiNum="+nearPoiNum+" 参数必须为整数");
		}
	   }
	  
	   map.put("onPhoneBind",onPhoneBind);
	   map.put("phoneBindFlag", phoneBindFlag);
	   map.put("unique",unique);  
       map.put("locationLen",locationLen);
       map.put("checkInDescLen",checkInDescLen);
       return map;
    }
    /**
     * 根据人员编号和人员库获得绑定的签到点设置
     * @return
     * @throws SQLException 
     */
    public Map getCheckInPointsInfo(String a0100,String nbase) throws SQLException{
     RowSet rs = null;
	 Map map = new HashMap();
	try {
		 
         String sql = "";
		 String checkInPoints = "";
		 String pointNames ="";
		 ContentDAO dao = new ContentDAO(this.conn);
         sql = "select p.location,p.name from kq_sign_point_emp e,kq_sign_point p where p.pid = e.pid and e.nbase = '"+nbase+"' and e.A0100 = '"+a0100+"'";
         rs = dao.search(sql);
		while (rs.next()) {
			map = new HashMap();
			String location = rs.getString("location") ;
			if(location!=null){
				checkInPoints+=location+";";
			}
			String name = rs.getString("name") ;
			if(name!=null){
				pointNames+=name+";";
			}
		}
		DbWizard  dbWizard = new DbWizard(conn);
		boolean isExistTable =dbWizard.isExistTable("kq_sign_point_org");
		if(checkInPoints.length()==0&&isExistTable){
			Map orgPointsMap =getCheckInOrgPointsInfo(this.userView);
			if(orgPointsMap!=null)
				map.putAll(orgPointsMap);
			else{
				map.put("checkInPoints",checkInPoints);
				map.put("pointNames",pointNames);
			}
		}else{
			map.put("checkInPoints",checkInPoints);
			map.put("pointNames",pointNames);
		}
		
		//获取签到范围距离
		sql = "select str_value from constant where constant='KQ_POINT_RADIUS'";
		rs = dao.search(sql);
		String pointRadius ="";
		if(rs.next()){
			 pointRadius = rs.getString("str_value");
		}
			
		map.put("pointRadius",pointRadius);	
			
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException();
        } finally {
            if(rs!=null){
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    	return map;
    }
    /**
     * 获取机构考勤点信息（单位，部门，岗位绑定的考勤点）
     * @return
     * @throws SQLException 
     */
    public Map getCheckInOrgPointsInfo(UserView userView) throws SQLException{
	   	 RowSet rs = null;
	   	 Map map = new HashMap();
	   	 try {    		 
	   		 if(userView==null){
	   			 return null;
	   		 }
	   		 String unitCodeitem = userView.getUserOrgId();
	   		 String depCodeitem = userView.getUserDeptId();
	   		 String posCodeitem = userView.getUserPosId();
	   		 StringBuffer sql = new StringBuffer("");
	   		 Map pointsMap = new HashMap();
	   		 ContentDAO dao = new ContentDAO(this.conn);
	   		 sql.append("select p.location,p.name,o.codesetid from kq_sign_point_org o,kq_sign_point p ");
	//            sql = "select p.location,p.name,o.codesetid from kq_sign_point_org o,kq_sign_point p where p.pid = o.pid and( 1=2 ";
	   		 String sqlWhere = " where p.pid = o.pid and( 1=2 ";
	            if(unitCodeitem!=null&&unitCodeitem.length()!=0){
	//           	 sql+=" or o.codeitemid = '"+unitCodeitem+"'";
	           	 sqlWhere+=" or '"+unitCodeitem+"' like o.codeitemid +'%'";
	            }
	   		 
	   		 if(depCodeitem!=null&&depCodeitem.length()!=0){
	//   			 sql+=" or o.codeitemid like '"+depCodeitem+"%'";
	   			 sqlWhere+=" or '"+depCodeitem+"' like o.codeitemid +'%'";
	   		 } 
	   		 if(posCodeitem!=null&&posCodeitem.length()!=0){
	//   			 sql+=" or o.codeitemid like '"+posCodeitem+"%'";
	   			 sqlWhere+=" or '"+posCodeitem+"' like o.codeitemid +'%'";
	   		 }
	   		 sqlWhere+=")";
	   		 
	   		 sql.append(sqlWhere);
	   		 // 获取距离最近的机构编码的考勤点
	   		 sql.append(" and len(o.codeitemid)=");
	   		 sql.append("(select max(len(o.codeitemid)) from kq_sign_point_org o,kq_sign_point p ");
	   		 sql.append(sqlWhere).append(")");
	   		 
	   		 rs = dao.search(sql.toString());
	   		 while (rs.next()) {
	
	   			 String codesetid = rs.getString("codesetid").toUpperCase() ;
	           	 String location = rs.getString("location") ;
	           	 String name = rs.getString("name") ;
	           	 if(codesetid!=null&&codesetid.length()!=0){
	   				if(pointsMap.containsKey(codesetid)){
	   					Map pointsInfoMap =(Map)pointsMap.get(codesetid);
	   					if(location!=null){
	   						location=pointsInfoMap.get("checkInPoints")+";"+location;
	   	    			}
	   					pointsInfoMap.put("checkInPoints", location);
	   					if(name!=null){
	   						name=pointsInfoMap.get("pointNames")+";"+name;
	   	    			}
	   					pointsInfoMap.put("pointNames", name);
	   				}else{
	   					Map pointsInfoMap = new HashMap(); 
	   					pointsInfoMap.put("checkInPoints", location);
	   					pointsInfoMap.put("pointNames", name);
	   					pointsMap.put(codesetid, pointsInfoMap);
	   				}
	           	 }
	           	 else{
	   				continue;
	           	 }
	   		 }
	   		 // 根据优先顺序
	   		 String codesetid = "@K";
	   		 if(!pointsMap.containsKey(codesetid)){
	   			codesetid = "UM";
	   		 }
	   		 if(!pointsMap.containsKey(codesetid)){
	   			codesetid = "UN";
	   		 }
	   		 if(!pointsMap.containsKey(codesetid)){
	   			return null;
	   		 }
	   		 Map points =(Map)pointsMap.get(codesetid);
	   		 if(points!=null){
	   			map.putAll(points)	;
	   		 }
 		} catch (SQLException e) {
           	e.printStackTrace();
           	throw new SQLException();
 		} finally {
   			PubFunc.closeDbObj(rs);
       	}
	   	return map;
    }
    /**
     * 得到卡号
     * 
     * @param nbase
     * @param a0100
     * @return
     * @throws GeneralException
     */
    public String getKqCard(String nbase, String a0100) throws GeneralException {
        String kq_cardno="";
        try {
            kq_cardno = (String)new Parameter(conn).ReadParameterXml().get("cardno");
        } catch (SQLException e1) {
            e1.printStackTrace();
            throw GeneralExceptionHandler.Handle(new GeneralException("",
                    ResourceFactory.getProperty("kq.netsign.error.notsetcardfield"), "", ""));
        }
        if (kq_cardno == null || kq_cardno.length() <= 0) {
            throw GeneralExceptionHandler.Handle(new GeneralException("",
                    ResourceFactory.getProperty("kq.netsign.error.notsetcardfield"), "", ""));
        }
        
        StringBuffer sql = new StringBuffer();
        sql.append("select " + kq_cardno + " from " + nbase + "a01 ");
        sql.append(" where a0100='" + a0100 + "'");
        ContentDAO dao = new ContentDAO(this.conn);
        String cardno = "";
        RowSet rs = null;
        try {
            rs = dao.search(sql.toString());
            if (rs.next()) {
                cardno = rs.getString(kq_cardno);
            }
            
            if (cardno == null || cardno.length() <= 0) {
                throw GeneralExceptionHandler.Handle(new GeneralException("",
                        ResourceFactory.getProperty("kq.netsign.error.notsetcard"), "", ""));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null)
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }
        return cardno;
    }
   

   
   
   
    
    /**
     * @throws SQLException 
     * @Title: isRecordExist   
     * @Description: 判断记录是否存在   
     * @param @param table 表名
     * @param @param whr sql条件
     * @param @return 
     * @return boolean    
     * @throws
     */
    public boolean isRecordExist(String table, String whr) throws SQLException {
        boolean exist = false;
        
        StringBuffer sb = new StringBuffer();
        sb.append("select 1 from ");
        sb.append(table);
        sb.append(" where ");
        sb.append(whr);
        
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try {
            rs = dao.search(sb.toString());
            exist = rs.next(); 
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if(rs!=null){
                rs.close();
            }
        }
        
        return exist;
    }
    /**
     * @throws ParseException 
     * 
     * @Title: searchLocus   
     * @Description:根据人员编号，人员库，时间段进行打卡记录查询    
     * @param @param a0100 人员编号
     * @param @param nbase 人员库
     * @param @param startDate 开始时间
     * @param @param endDate 结束时间
     * @param @return
     * @param @throws SQLException 
     * @return List    List<HashMap<String,String> >
     * @throws GeneralException 
     */
    public List searchLocus(String a0100,String nbase, String startDate,String endDate,String pageIndex,String pageSize) throws SQLException, ParseException, GeneralException{
        ArrayList list = null;
        RowSet rs = null;
        HashMap map = null;
        int index, size;
        index = Integer.parseInt(pageIndex);
        size = Integer.parseInt(pageSize);
        ArrayList kqNbaseList = new ArrayList();
        
        if(nbase==null||"".equals(nbase.trim())){
            kqNbaseList = getKqNbaseList();
        }else{
            kqNbaseList.add(nbase);
        }
       String sql = "";
       for (int i = 0; i < kqNbaseList.size(); i++) {
           if(i!=0){
               sql += " union all "; 
           }
           nbase = (String)kqNbaseList.get(i);
           if("myEmpLocus".equals(a0100)){//查询权限人员下所有人的签到记录
               a0100 = "select "+nbase+"A01.a0100 from "+nbase+"A01 where ";
               StringBuffer where = new StringBuffer(" 1=1 ");
               //考勤权限范围
               String code =getKqPrivCode(); 
               String codeValue =getKqPrivCodeValue(); 
               //根据考勤权限过滤人员
               if(code!=null&&code.length()!=0){
                   if("UN".equalsIgnoreCase(code)){
                       where.append(" and "+nbase+"A01.B0110 like '"+codeValue+"%' ") ;  
                   }else if("UM".equalsIgnoreCase(code)){
                       where.append(" and "+nbase+"A01.E0122 like '"+codeValue+"%' ") ;  
                   }else if("@K".equalsIgnoreCase(code)) {
                       where.append(" and "+nbase+"A01.E01A1 like '"+codeValue+"%' ") ; 
                   }
               }else{//没有权限
                   where.append(" and 1=2 ") ;
               }
               //zxj 20170504 与考勤模块权限保持一致
               where.append(" and a0100 in (select a0100").append(RegisterInitInfoData.getWhereINSql(userView, nbase)).append(")");
               a0100 = a0100+where;
           }
           sql += " select '"+i+"'"+Sql_switcher.concat()+"nbase as nbase ,a0100, work_date, work_time, a0101, oper_cause, location, longitude, latitude ,iscommon from  "
                       + this.table_name+ " where 1=1 "
                       +" and  a0100 in("+a0100+" )"
                       +" and  nbase="+"'"+nbase+"'"
                       +" and  work_date>="+"'"+startDate+"'"
                       +" and  work_date<="+"'"+endDate+"'"
                       +" and  datafrom='2' ";
    }
       if(kqNbaseList.size()==0){
           return list;
       }
       sql= "select * from ( select  ROW_NUMBER() over(order by nbase,a0100 ,work_date desc,work_time desc) numberCode "+
            ",A.* from ("+sql.toString()+")A )T where numberCode between "+((index-1)*size+1)+" and "+(size*index);;
        try {
            ContentDAO dao = new ContentDAO(this.conn);
            
            rs = dao.search(sql);
            String date = "";
            list = new ArrayList();
            while (rs.next()) {
                map = new HashMap();
                date = rs.getString("work_date") ;
                date= date.replace(".", "-");
                map.put("date", date);
                map.put("time", rs.getString("work_time"));
                map.put("iscommon", rs.getString("iscommon"));
                map.put("name", rs.getString("a0101"));
                map.put("addr", rs.getString("location"));
                map.put("longitude", rs.getString("longitude"));
                map.put("latitude", rs.getString("latitude"));
                map.put("desc", rs.getString("oper_cause"));//oper_cause 打卡说明
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date d =  sdf.parse(date);
                map.put("week", getWeek(d));
                list.add(map);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException();
        } finally {
            if(rs!=null){
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }
    /**
     * 
     * @Title: getKqNbaseList   
     * @Description:    根据考勤配置人员库和权限人员库控制人员库
     * @param @return 
     * @return List    
     * @throws GeneralException 
     * @throws SQLException 
     * @throws
     */
    public ArrayList getKqNbaseList() throws GeneralException, SQLException {
        ArrayList kqNbaseList=new ArrayList();
        Parameter parameter = new Parameter(conn);
        String xmlContent = parameter.search_KQ_PARAMETER();
        //tiany 修改考勤人员库不在针对每个单位设置 而是统一使用一个
       HashMap map = parameter.ReadOneParameterXml(xmlContent,"UN");
      String kqNbaseStr =(String) map.get("nbase");
      if(kqNbaseStr==null||kqNbaseStr.length()==0){
    	  throw GeneralExceptionHandler.Handle(new GeneralException("", ResourceFactory.getProperty("kq.register.dbase.nosave"), "", ""));
      }
      ArrayList dbaselist=userView.getPrivDbList(); //求应用库前缀权限列表      
      for(int i=0;i<dbaselist.size();i++){
          String userbase=dbaselist.get(i).toString();
          if(kqNbaseStr.indexOf(userbase)!=-1){
              kqNbaseList.add(userbase);
          }
      }
        return kqNbaseList;
    }
    /**
     * 得到考勤范围编码值
     * 先看考勤管理范围，如果没有着按人员范围取值
     * @param userView
     * @return
     */
    public  String getKqPrivCodeValue()
    {
        if(userView.isSuper_admin())
            return "";
        String privCodeValue=userView.getKqManageValue();
        if(privCodeValue!=null&&privCodeValue.length()>0)
            privCodeValue=privCodeValue.substring(2);
        else
            privCodeValue=userView.getManagePrivCodeValue();
        return privCodeValue;
    }
    /**
     * 得到考勤范围code
     * 先看考勤管理范围，如果没有着按人员范围取值
     * @param userView
     * @return
     */
    public  String getKqPrivCode()
    {
        if(userView.isSuper_admin())
            return "UN";
        String privCode=userView.getKqManageValue();
        if(privCode!=null&&privCode.length()>0)
            privCode=privCode.substring(0,2);
        else
            privCode=userView.getManagePrivCode();
        return privCode;
    }
  //根据日期取得星期几  
    public static String getWeek(Date date){   
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");  
        String week = sdf.format(date);  
        return week;  
    }
    
    /**
     * 
     * @Title: getCheckOutInfo   
     * @Description: 获取周边签到点   
     * @return 
     * @return List
     * @throws GeneralException 
     */
    public List getCheckOutInfo(String location) throws GeneralException {
    	List list = new ArrayList();
    	StringBuffer strsql = new StringBuffer();
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(conn);
		try {
			String[] locations =  location.split(",");
			float one = Float.parseFloat(locations[0]);
			float two = Float.parseFloat(locations[1]);
			
			strsql.append("select * from kq_sign_point p ");
			// 取出本人考勤点
			Map mapCheckIn = this.getCheckInPointsInfo(this.userView.getA0100(), this.userView.getDbname());
			String checkIn = (String) mapCheckIn.get("pointNames");
			// 需要去除本人当前的考勤点
			if (checkIn != null && checkIn.length() > 0) {
				String[] checkInList = checkIn.split(";");
				if (checkInList.length >= 1) {
					strsql.append(" where p.name  not in (");
					StringBuffer checkInSql = new StringBuffer();
					for (int i = 0; i < checkInList.length; i++) {
						checkInSql.append(",'" + checkInList[i] + "'");
					}
					strsql.append(checkInSql.substring(1) + ")");
				}
			}
			/** 常用查询条件列表 */
			rs = dao.search(strsql.toString());
			// 算法思路:运用hashMap分桶策略；耗时：na+15b->O(n)
			// 1桶 精度0.0008
			List barrel1 = new ArrayList();
			// 2桶 精度0.001
			List barrel2 = new ArrayList();
			// 3桶 精度0.004
			List barrel3 = new ArrayList();
			// 4桶 精度0.007
			List barrel4 = new ArrayList();
			// 5桶 精度0.01
			List barrel5 = new ArrayList();
			while (rs.next()) {
				// 116.361616,39.956958
				location = rs.getString("location");
				locations =  location.split(",");
				float oneP = Float.parseFloat(locations[0]);
				oneP = Math.abs(oneP - one);
				float twoP = Float.parseFloat(locations[1]);
				twoP = Math.abs(twoP - two);
				// 考勤点
				HashMap map = new HashMap();
				map.put("location", location);
				map.put("name", rs.getString("name"));
				// 分桶
				if (oneP < 0.0008 && twoP < 0.0008) {
					barrel1.add(map);
				} else if (oneP < 0.001 && twoP < 0.001) {
					barrel2.add(map);
				} else if (oneP < 0.004 && twoP < 0.004) {
					barrel3.add(map);
				} else if (oneP < 0.007 && twoP < 0.007) {
					barrel4.add(map);
				} else if (oneP < 0.01 && twoP < 0.01) {
					barrel5.add(map);
				}
			}
			
			// 考勤点合并
			int returnSize = 15;
			for (int i = 0; i < barrel1.size(); i++)
			{
				list.add(barrel1.get(i));
				if (list.size() > returnSize)
					return list;
			}
			for (int i = 0; i < barrel2.size(); i++)
			{
				list.add(barrel2.get(i));
				if (list.size() > returnSize)
					return list;
			}
			for (int i = 0; i < barrel3.size(); i++)
			{
				list.add(barrel3.get(i));
				if (list.size() > returnSize)
					return list;
			}
			for (int i = 0; i < barrel4.size(); i++)
			{
				list.add(barrel4.get(i));
				if (list.size() > returnSize)
					return list;
			}
			for (int i = 0; i < barrel5.size(); i++)
			{
				list.add(barrel5.get(i));
				if (list.size() > returnSize)
					return list;
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			throw GeneralExceptionHandler.Handle(sqle);
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return list;
	}

}
