package com.hjsj.hrms.transaction.general.inform.emp;

import com.hjsj.hrms.businessobject.general.inform.CorField;
import com.hjsj.hrms.businessobject.performance.workdiary.WeekUtils;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>
 * Title:AddEmpMainTrans.java
 * </p>
 * <p>
 * Description:保存人员主集
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2008-12-24 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class SaveEmpMainTrans extends IBusiness
{
    public void execute() throws GeneralException
    {

	HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
	String dbname=(String)hm.get("dbname");
	this.getFormHM().put("dbname", dbname);
	String fieldsetid = dbname+"A01";
	String a0100 = (String) this.getFormHM().get("a0100");

	ArrayList fieldlist = (ArrayList) this.getFormHM().get("fieldslist");

	ContentDAO dao = new ContentDAO(this.getFrameconn());
	try
	{
	    RecordVo vo = new RecordVo(fieldsetid);
	    vo.setString("a0100", a0100);
	    vo = dao.findByPrimaryKey(vo);

	    Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
		String chk = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"1","name"); //身份证指标
		chk=chk!=null?chk:"";
		String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","name"); //验证唯一性指标
		onlyname=onlyname!=null?onlyname:"";
		String chkvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"1","valid");//身份证验证是否启用
		chkvalid=chkvalid!=null?chkvalid:"";	
		String uniquenessvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","valid");//唯一性验证是否启用
		uniquenessvalid=uniquenessvalid!=null?uniquenessvalid:"";
		String dbchk = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"1","db");//验证身份证适用的人员库
		dbchk=dbchk!=null?dbchk:"";
		String dbonly = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","db");//验证唯一性适用的人员库
		dbonly=dbonly!=null?dbonly:"";
		
		DbNameBo dbnamebo = new DbNameBo(this.getFrameconn());
		
	    for (int i = 0; i < fieldlist.size(); i++)
	    {
		FieldItem fieldItem = (FieldItem) fieldlist.get(i);
		    String pri = this.userView.analyseFieldPriv(fieldItem.getItemid());
		    if("1".equals(pri))//只读
			continue;
		    else if("0".equals(pri))//没有权限
			continue;
		String itemid = fieldItem.getItemid();
		String value = fieldItem.getValue(); 
		if(fieldItem.isMainSet()&& "A01".equalsIgnoreCase(fieldItem.getFieldsetid())){
			if("1".equals(uniquenessvalid)){
				if(dbonly.trim().length()>2&&dbonly.toUpperCase().indexOf(dbname.toUpperCase())!=-1){
					if(itemid.equalsIgnoreCase(onlyname)&&value!=null&&value.trim().length()>0){
						String onlynameflag = dbnamebo.checkOnlyName(dbonly,fieldItem.getFieldsetid(),itemid,value,a0100);
						if(!"true".equalsIgnoreCase(onlynameflag))
							throw new GeneralException(onlynameflag);
					}
				}
			}
			if("1".equals(chkvalid)){
				if(dbchk.trim().length()>2&&dbchk.toUpperCase().indexOf(dbname.toUpperCase())!=-1){
					if(itemid.equalsIgnoreCase(chk)){
						CorField cof = new CorField();
						String sex = vo.getString(cof.getItemid(CorField.SEX_ITEMID, this.frameconn));
						sex=sex!=null&&sex.trim().length()>0?sex:"";
						
						String birthday = vo.getString(cof.getItemid(CorField.BIRTHDAY_ITEMID, this.frameconn));
						birthday=birthday!=null&&birthday.trim().length()>0?birthday:"";
						if(birthday.trim().length()>0){
							WeekUtils weekUtils = new WeekUtils();
							birthday=weekUtils.dateTostr(weekUtils.strTodate(birthday));
							birthday=birthday.replaceAll("-", "").replaceAll("\\.","");
						}
						
						if(value!=null&&value.trim().length()>0){
							String check = dbnamebo.checkIdNumber(value,birthday,sex);
							String arr[]= check.split(":");
							if(arr.length==2){
								if("false".equalsIgnoreCase(arr[0]))
									throw new GeneralException(arr[1]);
							}
							String onlynameflag = dbnamebo.checkOnlyName(dbchk,fieldItem.getFieldsetid(),itemid,value,a0100);
							if(!"true".equalsIgnoreCase(onlynameflag))
								throw new GeneralException(onlynameflag);
							else{
								onlynameflag = dbnamebo.checkOnlyName(dbchk,fieldItem.getFieldsetid(),itemid,
										dbnamebo.changeCardID(value, birthday),a0100);
								if(!"true".equalsIgnoreCase(onlynameflag))
									throw new GeneralException(onlynameflag);
						
							}
						}
					}
				}
			}
		}

		if ("".equals(value))
		    continue;

		if ("D".equals(fieldItem.getItemtype()))
		{	
		    if(value.length()>=10)
			value = PubFunc.replace(value, ".", "-");
		    else
			throw new GeneralException("字段["+fieldItem.getItemdesc()+"]日期格式不对！");
		    vo.setDate(itemid, value);
		} else if ("N".equals(fieldItem.getItemtype()))// 对于数值类型，在前后台都要进行控制,前台验证是整数还是小数类型，后台修正小数位数
		{
		    value = PubFunc.round(value, fieldItem.getDecimalwidth());
		    vo.setString(itemid, value);
		} else
		    vo.setString(itemid, value);
	    }

	    dao.updateValueObject(vo);
	} catch (SQLException e)
	{
	    e.printStackTrace();
	}

    }

}
