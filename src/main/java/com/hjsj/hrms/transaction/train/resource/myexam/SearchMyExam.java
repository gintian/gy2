package com.hjsj.hrms.transaction.train.resource.myexam;

import com.hjsj.hrms.businessobject.train.trainexam.exam.TrainExamPlanBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>
 * Title:SearchMyExam
 * </p>
 * <p>
 * Description:查询符合条件的试题信息
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2011-10-18
 * </p>
 * 
 * @author wangzhongjun
 * @version 1.0
 *  
 */
public class SearchMyExam extends IBusiness{

	
	public void execute() throws GeneralException {
		// 计划名称
		String planName = (String) this.getFormHM().get("planName");
		// 状态
		String state = (String) this.getFormHM().get("state");
		// 答卷模式
		String responseType = (String) this.getFormHM().get("responseType");
		
		//调用来源 0：非门户 5：门户
		HashMap params = (HashMap)this.getFormHM().get("requestPamaHM"); 
		String home = (String)params.get("home");
		params.remove("home");
		
		if (null == home)
        {
            home = "";
        }
		this.getFormHM().put("home", home);
		
		if (this.userView.getA0100() == null || this.userView.getA0100().length() <= 0) {
			
			throw GeneralExceptionHandler.Handle(new GeneralException("非自助用户！"));
		}
		
		try {
			// 					发布状态、  计划id、 试卷编号、计划名称、开始时间、结束时间、时长、  答卷方式、  状态  线上线下
			String sql = "select b.r5515,a.r5400,a.r5300,a.r5401,a.r5405,a.r5406,a.r5407,a.r5409,a.r5411,a.r5408,b.r5513,b.r5519 ";
			String cols = "r5515,r5400,r5300,r5401,r5405,r5406,r5407,r5409,r5411,r5408,r5513,r5519";
			
			if(Sql_switcher.searchDbServer()==2){
	            int w = DataDictionary.getFieldItem("r5405", "r54").getItemlength();
	            String format = getDateFormat(w);
	            sql = sql.replace("a.r5405", "to_char(a.r5405,'" + format + "') as r5405");
	            
	            w = DataDictionary.getFieldItem("r5406", "r54").getItemlength();
	            format = getDateFormat(w);
	            sql = sql.replace("a.r5406", "to_char(a.r5406,'" + format + "') as r5406");
	            
	        }
			
			StringBuffer where = new StringBuffer();
			where.append("from r54 a left join  (select * from r55 where nbase='");
			where.append(this.userView.getDbname()+"' and a0100='"+this.userView.getA0100()+"') b on a.r5400=b.r5400 where b.r5400 is not null and a.r5411 in('04','05','06') and a.r5408=1 ");
			
			if (planName != null && planName.length() > 0) {
				where.append(" and a.r5401 like '%"+planName+"%'");
			} else {
				planName = "";
			}
			
			if (state != null && state.length() > 0 && (!"0".equals(state)) ) {
				where.append(" and a.r5411='"+state+"'");
			} else {
				state = "0";
			}
			
			if (responseType != null && responseType.length() > 0 && (!"0".equals(responseType)) ) {
				where.append(" and a.r5409="+responseType+"");
			} else {
				responseType = "0";
			}
			
			String order = " order by a.r5400 ";
			
			// 状态集合
			ArrayList stateList = new ArrayList();
			CommonData data = new CommonData("0", "全部");
			CommonData data2 = new CommonData("05", "启动");
			CommonData data3 = new CommonData("06", "结束");
			stateList.add(data);
			stateList.add(data2);
			stateList.add(data3);
			
			// 答卷方式集合
			ArrayList responseTypeList = new ArrayList();
			CommonData data4 = new CommonData("1", "整版");
			CommonData data5 = new CommonData("2", "单题");
			responseTypeList.add(data);
			responseTypeList.add(data4);
			responseTypeList.add(data5);
			
			HashMap map = new HashMap();
			StringBuffer sqlstr = new StringBuffer();
			sqlstr.append("select r55.r5400 from r55 join r54 on r55.r5400=r54.r5400 where a0100='");
			sqlstr.append(this.userView.getA0100());
			sqlstr.append("' and nbase='");
			sqlstr.append(this.userView.getDbname());
			sqlstr.append("'");
			sqlstr.append(" and r54.R5411='05'");
			
			ContentDAO dao = new ContentDAO(this.frameconn);
			this.frowset = dao.search(sqlstr.toString());
			while(this.frowset.next()){
			    String r5400 = this.frowset.getString("r5400");
                    TrainExamPlanBo bo = new TrainExamPlanBo(this.frameconn);
                    bo.loadMessageParam(r5400);
                    String autoCompute = bo.getAutoCompute().toString();
                    String autoRelease = bo.getAutoRelease().toString();
                    String enabled = bo.getEnabled().toString();
                    if("true".equalsIgnoreCase(autoCompute) && "true".equalsIgnoreCase(autoRelease)
                            && "true".equalsIgnoreCase(enabled))
                        map.put(r5400, bo.getTimes());
                    else
                        map.put(r5400, "0");
			}
			
			this.getFormHM().put("stateList", stateList);
			this.getFormHM().put("responseTypeList", responseTypeList);
			
			this.getFormHM().put("sql", sql);
			this.getFormHM().put("cols", cols);
			this.getFormHM().put("where", where.toString());
			this.getFormHM().put("order", order);
			this.getFormHM().put("timesmap", map);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//this.getFormHM().put("planName", "");
		this.getFormHM().put("state", state);
		this.getFormHM().put("responseType", responseType);
		
	}
	
	/**
     * 获取orcle的日期类型指标的格式
     * @param w
     * @return
     */
    private String getDateFormat(int w) {

        String format = "yyyy-MM-dd";
        if (w == 18)
            format = "yyyy-MM-dd HH24:mi:ss";
        if (w == 15)
            format = "yyyy-MM-dd HH24:mi";
        if (w == 7)
            format = "yyyy-MM";
        if (w == 4)
            format = "yyyy";

        return format;
    }

}
