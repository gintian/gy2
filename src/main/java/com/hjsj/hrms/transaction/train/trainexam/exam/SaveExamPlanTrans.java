package com.hjsj.hrms.transaction.train.trainexam.exam;

import com.hjsj.hrms.businessobject.train.TrainAddBo;
import com.hjsj.hrms.businessobject.train.trainexam.exam.TrainExamPlanBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.Date;
import java.sql.SQLException;
import java.util.*;

/**
 * <p>
 * Title:TrainAddTrans.java
 * </p>
 * <p>
 * Description:保存培训考试计划
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2011-11-19 13:00:00
 * </p>
 * 
 * @author zxj
 * @version 1.0
 * 
 */
public class SaveExamPlanTrans extends IBusiness
{

    public void execute() throws GeneralException
    {

	HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");

	TrainAddBo bo = new TrainAddBo("R54", this.frameconn);
	String primaryField = bo.getPrimaryField();	
	
	String hideSaveFlds = (String) this.getFormHM().get("hideSaveFlds");
	HashMap hideSaveFldsMap = bo.getInitValueMap(hideSaveFlds);

	String recTable = bo.getRecTable();
	ContentDAO dao = new ContentDAO(this.getFrameconn());

	String priFldValue = (String) hm.get("r5400");
	priFldValue = PubFunc.decrypt(SafeCode.decode(priFldValue));
	boolean isNew = bo.isNew(priFldValue);
	ArrayList fieldlist = (ArrayList) this.getFormHM().get("fieldlist");

	RecordVo vo = new RecordVo(recTable);
	for (int i = 0; i < fieldlist.size(); i++)
	{
	    FieldItem fieldItem = (FieldItem) fieldlist.get(i);

	    String itemid = fieldItem.getItemid();
	    String value = fieldItem.getValue();

	    if ("D".equals(fieldItem.getItemtype()))
	    {
			// 如果时r31表注意r3115,r3116的日期要精确到时分秒
			if ("r5405".equalsIgnoreCase(itemid))
			{
			    String r5405_time = (String) hm.get("r5405_time");
			    if(value!=null&&value.length()>0)
			    	value += " " + r5405_time;
			} else if ("r5406".equalsIgnoreCase(itemid))
			{
			    String r5406_time = (String) hm.get("r5406_time");
			    if(value!=null&&value.length()>0)
			    	value += " " + r5406_time;
			}
			vo.setDate(itemid, value);
	    } 
	    else if ("N".equals(fieldItem.getItemtype()))// 对于数值类型，在前后台都要进行控制,前台验证是整数还是小数类型，后台修正小数位数
	    {
	    	if("r5413".equalsIgnoreCase(fieldItem.getItemid())&&((value==null)||value.length()==0))
	    	  value = "-1";
	    	else
			  value = PubFunc.round(value, fieldItem.getDecimalwidth());
	    	double va = Double.parseDouble(value);
			vo.setDouble(itemid, va);
	    } 
	    else
	    	vo.setString(itemid, value);
	}
	// 保存主键字段
	vo.setString(primaryField, priFldValue);

	// 保存在页面不显示出来，但付了值需要保存的字段
	if (hideSaveFldsMap.size() > 0)
	{
	    Set fields = hideSaveFldsMap.keySet();
	    for (Iterator iter = fields.iterator(); iter.hasNext();)
	    {
		String field = (String) iter.next();
		String value = (String) hideSaveFldsMap.get(field);
		vo.setString(field, value);
	    }
	}

	if (isNew)
	{
		int nOrder = 1;
		String maxOrder = bo.getMaxValue("R54", "nOrder");
		if ((maxOrder!=null)&&(maxOrder.length()>0))
			nOrder = Integer.parseInt(maxOrder) + 1;
		
		TrainExamPlanBo planBo = new TrainExamPlanBo();
		int r5400 = planBo.getNewPlanId();		
		vo.setInt("r5400", r5400);
		
		Date date = DateUtils.getSqlDate(Calendar.getInstance());
		vo.setDate("create_time", date);
		vo.setString("create_user", userView.getUserName());
		vo.setInt("norder", nOrder);
	
	    dao.addValueObject(vo);
	} 
	else
	    try
	    {
	    	dao.updateValueObject(vo);
	    } 
		catch (SQLException e)
	    {
			e.printStackTrace();
	    }
   }

}
