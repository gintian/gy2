package com.hjsj.hrms.transaction.train.job;

import com.hjsj.hrms.businessobject.train.TrainClassBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class BrowseTrainClassListTran extends IBusiness {

	public void execute() throws GeneralException {
		String items = SystemConfig.getPropertyValue("train_self_class_items");
		ArrayList list = new ArrayList();
		String columns = "";
		String column = "";
		try {
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String classname = (String)hm.get("classname");
			classname = SafeCode.decode(classname);
			String wherestr = getwhere(classname);
			FieldItem fi = new FieldItem();
			fi = DataDictionary.getFieldItem("r3101", "r31");
			list.add(fi);
			fi = DataDictionary.getFieldItem("r3130", "r31");
			list.add(fi);
			columns += "r31.r3101,r31.r3130,";
			column += "r3101,r3130,";
			items = items.toLowerCase();
			if (items != null && items.length() > 4) {
				ArrayList fieldlistr31 = DataDictionary.getFieldList("r31", 1);
				String[] item = items.split(",");
				for(int i=0;i<item.length;i++){
					if("r3101".equalsIgnoreCase(item[i])|| "r3130".equalsIgnoreCase(item[i])
						|| "r4013".equalsIgnoreCase(item[i])|| "r4015".equalsIgnoreCase(item[i]))
						continue;
					for(int j=0;j<fieldlistr31.size();j++){
						fi = (FieldItem)fieldlistr31.get(j);
						if(item[i].equals(fi.getItemid()) && "1".equalsIgnoreCase(fi.getState())){
							list.add(fi);
							columns += "r31."+fi.getItemid()+",";
							column += fi.getItemid()+",";
						}
					} 
				}

			} else {

				fi = DataDictionary.getFieldItem("r3113", "r31");
				list.add(fi);
				fi = DataDictionary.getFieldItem("r3114", "r31");
				list.add(fi);
				fi = DataDictionary.getFieldItem("r3110", "r31");
				list.add(fi);
				columns += "r31.r3113,r31.r3114,r31.r3110,";
				column += "r3113,r3114,r3110,";
			}
			fi = DataDictionary.getFieldItem("r4013");
			list.add(fi);
			fi = DataDictionary.getFieldItem("r4015");
			list.add(fi);
			if(columns.indexOf("r3113")==-1){
				columns += "r31.r3113,";
				column += "r3113,";
			}
			if(columns.indexOf("r3114")==-1){
				columns += "r31.r3114,";
				column += "r3114,";
			}
			columns += "r40.r4013,r40.r4015";
			column += "r4013,r4015";
		
			if (this.userView.getA0100() == null || userView.getA0100().trim().length() < 1)
				throw new GeneralException(ResourceFactory.getProperty("selfservice.module.pri"));

			ContentDAO dao = new ContentDAO(this.frameconn);
			RowSet rs = null;
			if (userView.getStatus() == 4)// 帐号用户
			{

			} else if (userView.getStatus() == 0)// 用户管理中用户
			{
				// 对于用户管理中的用户要找到它关联的帐号用户 再进行判断 因为这个方法（isHaveResource）只适用于帐号用户

				String relaUserA0100 = userView.getA0100();

				String username = "";
				String password = "";
				String userDb = userView.getDbname();

				String usernameFld = "";
				String passwordFld = "";
				RecordVo login_vo = ConstantParamter.getConstantVo("SS_LOGIN_USER_PWD");
				if (login_vo != null) {
					String login_name = login_vo.getString("str_value");
					int idx = login_name.indexOf(",");
					if (idx != -1) {
						usernameFld = login_name.substring(0, idx);
						passwordFld = login_name.substring(idx + 1);
					}
				}
				if (usernameFld == null || "#".equals(usernameFld) || usernameFld.length() < 1)
					usernameFld = "username";
				if (passwordFld == null || "#".equals(passwordFld) || passwordFld.length() < 1)
					passwordFld = "userpassword";
				String sqlStr = "select " + usernameFld + "," + passwordFld + " from " + userDb + "A01 where a0100='" + relaUserA0100 + "'";
				rs = dao.search(sqlStr);
				if (rs.next()) {
					username = rs.getString(1);
					password = rs.getString(2) == null ? "" : rs.getString(2);
				}

				// 关联人员设置了自助用户，那么转为自助用户账号
				if (username != null) {
					userView = new UserView(username, password, this.frameconn);
					userView.canLogin();
				}
			}
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date date = new Date();
			String datetime = dateFormat.format(date);
			StringBuffer sql = new StringBuffer();
			sql.append("select " + columns + " from r31 ");
			sql.append(" left join ( select * from r40 where r4001='");
			sql.append(userView.getA0100());
			sql.append("' and nbase='");
			sql.append(userView.getDbname());
			sql.append("') r40  on r31.r3101=r40.r4005  where r31.r3127='04'"+wherestr);
			sql.append(" and "+Sql_switcher.dateToChar("r31.r3115", "yyyy-MM-dd")+">='"+datetime+"'");
			String orderby = " order by r31.r3113 desc";

			ArrayList classList = getMyTrainClassList(sql+orderby, list);
			this.getFormHM().put("trainClassList", classList);
			this.getFormHM().put("sql", sql.toString());
			this.getFormHM().put("list", list);
			this.getFormHM().put("columns", column);
			this.getFormHM().put("orderBy", orderby);
			this.getFormHM().put("a0100", SafeCode.encode(PubFunc.encrypt(this.getUserView().getA0100())));
			this.getFormHM().put("dbname", SafeCode.encode(PubFunc.encrypt(this.getUserView().getDbname())));
			this.getFormHM().put("classname", classname);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}
	/**
     * 获取查询条件
     * @param year
     * @param classname
     * @return
     */
	private String getwhere(String classname) {
		StringBuffer wherestr = new StringBuffer();
		if (classname != null && classname.length() > 0)
			wherestr.append(" AND r31.R3130 LIKE '%" + classname + "%'");
		return wherestr.toString();
	}
	/**
	 * 获取当前用户权限下的培训班
	 * @param sql 查询的sql语句
	 * @param columnlist 页面需要显示的指标
	 * @return list 可显示的培训班
	 * @throws GeneralException
	 */
    public ArrayList getMyTrainClassList(String sql, ArrayList columnlist) throws GeneralException {
        ArrayList list = new ArrayList();
        try {
            ContentDAO dao = new ContentDAO(this.frameconn);
            this.frowset = dao.search(sql);
            String r3113 = "";
            String r3114 = "";
            while (this.frowset.next()) {
                String r3101 = this.frowset.getString("r3101");
                boolean flag = TrainClassBo.isHaveRes(this.userView, r3101);
                if (!flag)
                    continue;

                LazyDynaBean abean = new LazyDynaBean();
                abean.set("r3101", SafeCode.encode(PubFunc.encrypt(r3101)));
                //判断培训班学员是否满员额1：未满     0：满额
                if(!TrainClassBo.checkStudentsCount(r3101))
                    abean.set("studentsCount", "1");
                else
                    abean.set("studentsCount", "0");
                for (int i = 0; i < columnlist.size(); i++) {
                    FieldItem fi = (FieldItem) columnlist.get(i);
                    if ("r3101".equalsIgnoreCase(fi.getItemid()))
                        continue;

                    if("D".equalsIgnoreCase(fi.getItemtype())){
                        String style = getDateStyle(fi);
                        Date d = this.frowset.getDate(fi.getItemid());
                        if(d == null)
                            continue;
                        
                        String date = DateUtils.format(this.frowset.getDate(fi.getItemid()), style);
                        if("r3113".equalsIgnoreCase(fi.getItemid()))
                            r3113=date;
                        
                        if("r3114".equalsIgnoreCase(fi.getItemid()))
                            r3114=date;
                        
                        abean.set(fi.getItemid(), date==null?"":date);
                    } else if("r4013".equalsIgnoreCase(fi.getItemid())){
                        String r4013 = this.frowset.getString(fi.getItemid());
                        abean.set(fi.getItemid(), r4013==null?"":r4013);
                    } else{
                        String value = this.frowset.getString(fi.getItemid());
                        abean.set(fi.getItemid(), value==null?"":value);
                    }

                }
                //判断培训班是否在报名时间内  1：在报名时间内  2：未到报名时间  3：报名时间已结束
                String isOverTime = "";
                if(!TrainClassBo.isOverTimes(r3113,r3114))
                    isOverTime = "1";
                else if(TrainClassBo.isOverTimes(r3113,null))
                    isOverTime = "2";
                else if(TrainClassBo.isOverTimes(null,r3114))
                    isOverTime = "3";
                
                abean.set("isOverTime", isOverTime);
                list.add(abean);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return list;
    }
    /**
     * 获取日期显示的格式
     * @param fi 日期指标
     * @return String 日期指标的格式
     */
    private String getDateStyle(FieldItem fi){
        String style = "yyyy.MM.dd";
        int length = fi.getItemlength();
        if(4 == length)
            style = "yyyy";
        else if(7 == length)
            style = "yyyy.MM";
        else if(15 == length)
            style = "yyyy.MM.dd HH:mm";
        else if(18 == length)
            style = "yyyy.MM.dd HH:mm:ss";
        
        return style;
    }
}
