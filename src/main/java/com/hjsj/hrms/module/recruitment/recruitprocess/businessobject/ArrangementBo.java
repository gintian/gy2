package com.hjsj.hrms.module.recruitment.recruitprocess.businessobject;

import com.hjsj.hrms.businessobject.infor.multimedia.PhotoImgBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
/***
 * 面试安排操作
 * @author Administrator
 *
 */
public class ArrangementBo {
	private Connection conn=null;
    private UserView userview;
    
    public ArrangementBo(Connection conn, UserView userview)
    {
    	 this.conn=conn;
    	 this.userview=userview;
    }
    /**
     * 获取选择人员简历信息
     * @param a0100 人员编号
     * @param nbase 人员库标志
     * @return
     */
    public LazyDynaBean getResumeInfo(String a0100,String nbase,String z0301)
    {
    	LazyDynaBean resumeInfo = new LazyDynaBean();
    	StringBuffer sql = new StringBuffer();
    	ArrayList list = new ArrayList();
    	try {			
    		ContentDAO dao = new ContentDAO(conn);
    		sql.append(" select a0101,");
    		
    		//移动电话
    		String c0104 = ConstantParamter.getMobilePhoneField().toLowerCase();
            if(c0104!=null && !"".equals(c0104))
			{
            	sql.append(c0104).append(" c0104,");
			}
            
            //邮件指标
            String c0102 = ConstantParamter.getEmailField().toLowerCase();
            if(c0102!=null && !"".equals(c0102))
			{
            	sql.append(c0102).append(" c0102,");
			}
            
            String age = SystemConfig.getPropertyValue("age"); //暂时从system获取年龄指标
            String sex = "A0107";
            if (age == null || "".equals(age)) {
                age = "A0112";
            }
            
            FieldItem ageFieldItem = this.getUsedField(age, "A01", "年龄");
            FieldItem sexFieldItem = this.getUsedField(sex, "A01", "性别");
            if(ageFieldItem!=null){
            	age = ageFieldItem.getItemid();
            	sql.append(age+" A0112,");
            }
            if(sexFieldItem!=null){
            	sex = sexFieldItem.getItemid();
            	sql.append(sex+" A0107,");
            }
            FieldItem A0410 = DataDictionary.getFieldItem("A0410");
            FieldItem A0435 = DataDictionary.getFieldItem("A0435");
            boolean appendA0410 = false;
            boolean appendA0435 = false;
            if(A0410!=null&&"1".equals(A0410.getUseflag())){
            	appendA0410 = true;
            	sql.append("A0410,");
            }
            if(A0435!=null&&"1".equals(A0435.getUseflag())){
            	appendA0435 = true;
            	sql.append("A0435,");
            }
            sql.append("Z0351,z0301,link_id from ");
    		sql.append(nbase+"A01 a01 inner join zp_pos_tache zpt ");
    		sql.append(" on a01.A0100=?  and a01.A0100=zpt.A0100 ");
    		sql.append(" inner join Z03 on z03.Z0301 = zpt.ZP_POS_ID and Z03.Z0301=? ");
    		sql.append(" left join "+nbase+"A04 a04 on a01.A0100 = a04.A0100 ");
    		list.add(a0100);
    		list.add(z0301);
    		RowSet rs = dao.search(sql.toString(), list);
    		
    		if(rs.next())
    		{
    			resumeInfo.set("a0100", a0100);
    			resumeInfo.set("nbase", nbase);
    			resumeInfo.set("a0101", rs.getString("a0101"));
                if(c0104!=null&&!"".equals(c0104))
    			{
                	resumeInfo.set("c0104", rs.getString("c0104")==null?"":rs.getString("c0104"));
    			}else{
    				resumeInfo.set("c0104", "");
    			}
                if(c0102!=null&&!"".equals(c0102))
    			{
                	resumeInfo.set("c0102", rs.getString("c0102")==null?"":rs.getString("c0102"));
    			}else{
    				resumeInfo.set("c0102", "");
    			}
    			resumeInfo.set("z0351", rs.getString("z0351"));
    			resumeInfo.set("z0301", rs.getString("z0301"));
    			if(sexFieldItem!=null){
                	resumeInfo.set("a0107", rs.getString("A0107")==null?"":rs.getString("A0107"));
                }
    			if(ageFieldItem!=null){
    				resumeInfo.set("a0112", rs.getString("A0112")==null?"":rs.getString("A0112"));
    			}
    			if(appendA0410)
    				resumeInfo.set("a0410", rs.getString("A0410")==null?"":rs.getString("A0410"));
    			if(appendA0435)
    				resumeInfo.set("a0435", rs.getString("A0435")==null?"":rs.getString("A0435"));
    		}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	return resumeInfo;
    }
	/***
	 * 获取当前人员面试安排序号
	 * @param a0100面试人员
     * @param nbase人员库
     * @param z0301职位id
     * @param arrangAddress面试地点
     * @param arrangeDate面试时间
     * @param examinerMail给面试官发送邮件
     * @param candidateMail给面试者发送邮件
     * @param candidateText给面试者发送短信
     * @param link_id流程id
	 * @return
	 */
    public String getArrangementId(String a0100,String nbase,String z0301,String arrangAddress,String arrangeDate,String examinerMail,String candidateMail,String candidateText,String link_id){
    	
    	String Z0501 = "";
    	try {
    		StringBuffer sqlStr = new StringBuffer("select Z0501 from Z05 ");
    		sqlStr.append(" where a0100=? and Z0301=? and NBASE=?  ");
    		ArrayList list = new ArrayList();
    		list.add(a0100);
    		list.add(z0301);
    		list.add(nbase);
    		
			ContentDAO dao = new ContentDAO(conn);
			RowSet rs = dao.search(sqlStr.toString(), list);
			if(rs.next())
			{
				Z0501 = rs.getString("Z0501");
				Z0501 = this.updateArrangement(arrangAddress, arrangeDate, examinerMail, candidateMail, candidateText, link_id, Z0501);
			}else{
				Z0501 = this.addArrangement(a0100, nbase, z0301, arrangAddress, arrangeDate, examinerMail, candidateMail, candidateText, link_id);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Z0501;
    }
    /***
     * 添加当前面试人员序号
     * @param a0100面试人员
     * @param nbase人员库
     * @param z0301职位id
     * @param arrangAddress面试地点
     * @param arrangeDate面试时间
     * @param examinerMail给面试官发送邮件
     * @param candidateMail给面试者发送邮件
     * @param candidateText给面试者发送短信
     * @param link_id流程id
     * @return
     */
    public String addArrangement(String a0100,String nbase,String z0301,String arrangAddress,String arrangeDate,String examinerMail,String candidateMail,String candidateText,String link_id){
    	String Z0501 = "";
    	try {
    		IDGenerator idg = new IDGenerator(2, this.conn);
            String az0501 = idg.getId("Z05.Z0501");
			StringBuffer sql = new StringBuffer();
			sql.append("insert into Z05(Z0501,A0100,Nbase,Z0301,Z0503,Z0509,Z0527,Z0529,Z0531,link_id) ");
			sql.append(" values(?,?,?,?,?,?,?,?,?,?)");
			ArrayList valueList = new ArrayList();
			valueList.add(az0501);
			valueList.add(a0100);
			valueList.add(nbase);
			valueList.add(z0301);
			valueList.add(arrangAddress);
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	        Timestamp dateValue = new Timestamp(df.parse(arrangeDate).getTime());
			valueList.add(dateValue);
			valueList.add("true".equalsIgnoreCase(examinerMail)?"1":"2");
			valueList.add("true".equalsIgnoreCase(candidateMail)?"1":"2");
			valueList.add("true".equalsIgnoreCase(candidateText)?"1":"2");
			valueList.add(link_id);
			ContentDAO dao = new ContentDAO(conn);
			dao.update(sql.toString(), valueList);
			Z0501 = az0501;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Z0501;
    }
    /***
     *保存面试安排时删除当前安排序号旧数据
     */
    public void removeArrange(String Z0501)
    {
    	try {
			String sql = "delete from zp_examiner_arrange where Z0501=?";
			ArrayList list = new ArrayList();
			list.add(Z0501);
			ContentDAO dao = new ContentDAO(conn);
			dao.update(sql, list);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    /***
     * 修改已有面试安排信息
     * @param arrangAddress面试地点
     * @param arrangeDate面试时间
     * @param examinerMail给面试官发送邮件
     * @param candidateMail给面试者发送邮件
     * @param candidateText给面试者发送短信
     * @param link_id流程id
     * @param Z0501序号
     * @return
     */
    public String updateArrangement(String arrangAddress,String arrangeDate,String examinerMail,String candidateMail,String candidateText,String link_id,String Z0501){
    	try {
			StringBuffer sql = new StringBuffer();
			sql.append("update Z05 set Z0503=?,Z0509=?,Z0527=?,Z0529=?,Z0531=?,link_id=? ");
			sql.append(" where Z0501=?");
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Timestamp dateValue = new Timestamp(df.parse(arrangeDate).getTime());
			ArrayList valueList = new ArrayList();
			valueList.add(arrangAddress);
			valueList.add(dateValue);
			valueList.add("true".equalsIgnoreCase(examinerMail)?"1":"2");
			valueList.add("true".equalsIgnoreCase(candidateMail)?"1":"2");
			valueList.add("true".equalsIgnoreCase(candidateText)?"1":"2");
			valueList.add(link_id);
			valueList.add(Z0501);
			ContentDAO dao = new ContentDAO(conn);
			dao.update(sql.toString(), valueList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Z0501;
    }
    /***
     * 保存面试安排考官信息
     * @param z0501面试安排序号
     * @param c0104考官电话号码
     * @param email考官邮箱
     * @param start_time开始时间
     * @param end_time结束时间
     * @param address面试地点
     * @param groupNum组号
     * @param a0100考官编号
     * @param nbase考官人员库
     * @param date创建时间
     * @param userName创建者用户名
     * @param userFullName创建者姓名
     * @throws SQLException 
     */
    public void saveArrangement(String z0501,String c0104,String email,String start_time,
    		String end_time,String address,int groupNum,String a0100,
    		String nbase,String date,String userName,String userFullName) throws SQLException
    {
    	IDGenerator idg = new IDGenerator(2, this.conn);
        try {
			String id = idg.getId("zp_examiner_arrange.id");
			StringBuffer sql = new StringBuffer();
			sql.append("insert into zp_examiner_arrange(id,Z0501,Phone_number,email,start_time,End_time,address,Group_number,A0100,Nbase,create_time,create_user,create_fullname) ");
			sql.append(" values(?,?,?,?,?,?,?,?,?,?,?,?,?)");
			ArrayList valueList = new ArrayList();
			valueList.add(id);
			valueList.add(z0501);
			valueList.add(c0104);
			valueList.add(email);
			valueList.add(start_time);
			valueList.add(end_time);
			valueList.add(address);
			valueList.add(groupNum+"");
			valueList.add(a0100);
			valueList.add(nbase);
			valueList.add(java.sql.Timestamp.valueOf(date));
			valueList.add(userName);
			valueList.add(userFullName);
			ContentDAO dao = new ContentDAO(conn);
			dao.update(sql.toString(), valueList);
		} catch (GeneralException e) {
			e.printStackTrace();
		}
    }
    /***
     * 获取面试安排信息
     * @param z0301 职位id
     * @param a0100 人员编号
     * @param nbase 人员库
     * @param link_id 流程
     * @return
     */
    public LazyDynaBean getArrangementInfo(String z0301,String a0100,String nbase,String link_id){
    	LazyDynaBean bean = new LazyDynaBean();
    	try {
			StringBuffer sql = new StringBuffer("select Z0501,Z0503,Z0509,Z0527,Z0529,Z0531 from Z05 ");
			sql.append(" where Z0301=? and A0100=? and nbase=? and link_id=?");
			ArrayList values = new ArrayList();
			values.add(z0301);
			values.add(a0100);
			values.add(nbase);
			values.add(link_id);
			ContentDAO dao = new ContentDAO(conn);
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			RowSet rs = dao.search(sql.toString(), values);
			if(rs.next())
			{
				bean.set("Z0501", rs.getString("Z0501"));//序号
				bean.set("Z0503", rs.getString("Z0503"));//面试地点
				bean.set("Z0509", dateFormat.format(rs.getTimestamp("Z0509")));//面试时间
				bean.set("Z0527", rs.getString("Z0527"));//邮件通知面试官
				bean.set("Z0529", rs.getString("Z0529"));//邮件通知候选人
				bean.set("Z0531", rs.getString("Z0531"));//短信通知候选人
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return bean;
    }
    /***
     * h获取面试面信息集合
     * @param Z0501
     * @return
     */
    public ArrayList getInterviewerInfo(String Z0501)
    {
    	ArrayList list = new ArrayList();
    	try {
    		ContentDAO dao = new ContentDAO(conn);
    		StringBuffer groupsql = new StringBuffer("select group_number from zp_examiner_arrange where z0501=? group by group_number");
    		ArrayList groupValues = new ArrayList();
    		groupValues.add(Z0501);
    		RowSet groupRS = dao.search(groupsql.toString(),groupValues);
			while(groupRS.next())
			{
				ArrayList interviewerInfo = new ArrayList();
				//获取每组信息
				String groupNo = groupRS.getString("group_number");
				StringBuffer sql = new StringBuffer("select ");
				sql.append(Sql_switcher.isnull("Phone_number","0")+" Phone_number, ");
				sql.append(Sql_switcher.isnull("email","0")+" email, ");
				sql.append("start_time,End_time,address,Group_number,A0100,Nbase,create_time,create_user,create_fullname ");
				sql.append(" from zp_examiner_arrange where Z0501=? and group_number=?");
				ArrayList values = new ArrayList();
				values.add(Z0501);
				values.add(groupNo);
				RowSet rs = dao.search(sql.toString(), values);
				LazyDynaBean beanInfo = new LazyDynaBean();
				String userNames = "";
				String Phone_numbers = "";
				String emails = "";
				String NbaseA0100s = "";
				while(rs.next())
				{
					LazyDynaBean bean = new LazyDynaBean();
					if(StringUtils.isEmpty(rs.getString("Nbase"))){
						bean.set("photoPath", "");//用户头像信息
						bean.set("userName", "");//移动电话号码
						userNames += ""+",";
						bean.set("NbaseA0100", "");//人员编号及人员库
						NbaseA0100s += ""+",";
					}else{
						bean.set("photoPath", this.getPhotoPath(rs.getString("Nbase"), rs.getString("A0100")));//用户头像信息
						bean.set("userName", this.getName(rs.getString("Nbase"), rs.getString("A0100")));//移动电话号码
						userNames += this.getName(rs.getString("Nbase"), rs.getString("A0100"))+",";
						bean.set("NbaseA0100", PubFunc.encrypt(rs.getString("Nbase")+rs.getString("A0100")));//人员编号及人员库
						NbaseA0100s += PubFunc.encrypt(rs.getString("Nbase")+rs.getString("A0100"))+",";
					}
					bean.set("Phone_number", "0".equals(rs.getString("Phone_number"))?"":rs.getString("Phone_number"));//移动电话号码
					Phone_numbers += ("0".equals(rs.getString("Phone_number"))?"":rs.getString("Phone_number"))+" ,";
					bean.set("email", "0".equals(rs.getString("email"))?"":rs.getString("email"));//邮箱地址
					emails += ("0".equals(rs.getString("email"))?"":rs.getString("email"))+" ,";
					beanInfo.set("start_time", rs.getString("start_time"));//面试起始时间
					beanInfo.set("End_time", rs.getString("End_time"));//面试结束时间
					beanInfo.set("address", rs.getString("address"));//面试地点
					beanInfo.set("Group_number", rs.getString("Group_number"));//组号
					bean.set("A0100", rs.getString("A0100"));//人员编号
					bean.set("Nbase", rs.getString("Nbase"));//人员库
					beanInfo.set("create_time", rs.getTimestamp("create_time"));//开始时间
					beanInfo.set("create_user", rs.getString("create_user"));//创建用户
					beanInfo.set("create_fullname", rs.getString("create_fullname"));//创建者姓名
					interviewerInfo.add(bean);
					beanInfo.set("interviewerInfo", interviewerInfo);
				}
				beanInfo.set("userNames", userNames);
				beanInfo.set("Phone_numbers", Phone_numbers);
				beanInfo.set("emails", emails);
				beanInfo.set("NbaseA0100s",NbaseA0100s);//加密每组内所有人员
				//beanInfo.set("beanInfo", beanInfo);//将每组人员信息放入集合中
				
				list.add(beanInfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return list;
    }
    
    /**
     * 获取人员头像
     * @param nbase 人员库
     * @param a0100 人员编号
     * @return
     */
    private String getPhotoPath(String nbase, String a0100) {
        PhotoImgBo imgBo = new PhotoImgBo(conn);
        return imgBo.getPhotoPathLowQuality(nbase, a0100);
    }
    /**
     * 获取用户名
     * @param nbase
     * @param a0100
     * @return
     */
    private String getName(String nbase,String a0100)
    {
    	String userName = "";
    	try {
			
    		ContentDAO dao = new ContentDAO(conn);
    		StringBuffer sql = new StringBuffer("select A0101 from "+nbase+"A01 where a0100=?");
    		ArrayList values = new ArrayList();
    		values.add(a0100);
    		RowSet rs = dao.search(sql.toString(),values);
    		if(rs.next())
    		{
    			userName = rs.getString("A0101");
    		}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userName;
    }
    /***
     * 修改面试安排状态
     * @param z0301 职位序号
     * @param a0100 人员编号
     * @param link_id 流程编号
     */
    public void updateResume(String z0301,String a0100,String link_id)
    {
    	try {
    		StringBuffer sql = new StringBuffer("update zp_pos_tache set resume_flag=? where A0100=? and ZP_POS_ID=? and link_id=?");
    		ArrayList list = new ArrayList();
    		list.add("0502");
    		list.add(a0100);
    		list.add(z0301);
    		list.add(link_id);
			ContentDAO dao = new ContentDAO(conn);
			dao.update(sql.toString(), list);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    /**
	 * @param itemId 需要用的字段名称
	 * @param itemSet 所在子集
	 * @param itemdesc 如果所需字段名称没拿到对应fieldItem的话，需要根据描述去查
	 * @return
	 */
	public FieldItem getUsedField(String itemId,String itemSet,String itemdesc){
		FieldItem item = DataDictionary.getFieldItem(itemId, itemSet);
        ArrayList<FieldItem> fieldList = DataDictionary.getFieldList(itemSet, Constant.USED_FIELD_SET);
        if(item==null||!(Constant.USED_FIELD_SET+"").equals(item.getUseflag())){
			for (FieldItem fieldItem : fieldList) {
				if(itemdesc.equals(fieldItem.getItemdesc()))
					itemId = fieldItem.getItemid();
			}
        }
        
        FieldItem needItem = DataDictionary.getFieldItem(itemId, itemSet);
        if(needItem != null && !"1".equals(needItem.getUseflag()))
        	return null;
        
		return needItem;
	}
}
