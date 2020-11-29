package com.hjsj.hrms.transaction.train.trainexam.exam;

import com.hjsj.hrms.businessobject.train.TrainAddBo;
import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.businessobject.train.trainexam.exam.TrainExamPlanBo;
import com.hjsj.hrms.utils.FormatValue;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.common.FieldItemView;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

public class EditExamPlanTrans extends IBusiness {

	public void execute() throws GeneralException
	{		
		String r5400 = "";
		HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		String e_flag = (String)hm.get("e_flag");
		if((e_flag!=null)&&(!"add".equalsIgnoreCase(e_flag)))
		    r5400 = (String)hm.get("r5400");	
		r5400 = PubFunc.decrypt(SafeCode.decode(r5400));
		   
		String initValue = "r5411:01,r5410:1,r5408:0,r5409:0,r5415:1,r5417:-1";// 需要初始化的字段1:初始值1,初始值字段2:初始值2,
		String hideFlds = "create_user,create_time,";// 不需要显示的字段
		String itemidarr = (String) hm.get("readonlyFilds");// 只读指标
	                                                               
		String hidepics = "imgr5411";// 隐藏字段旁边辅助输入的图片：imgr3127,imgr3128
		String hideSaveFlds = (String) hm.get("hideSaveFlds");
		String isUnUmRela = (String) hm.get("isUnUmRela");

		hm.remove("r5400");
		hm.remove("initValue");
		hm.remove("hideFilds");
		hm.remove("a_code");
		hm.remove("readonlyFilds");
		hm.remove("hidepics");
		hm.remove("hideSaveFlds");
		hm.remove("isUnUmRela");

		initValue = SafeCode.decode(initValue); // 初始值是中文名称时候需要转码
		this.getFormHM().put("initValue", isNull(initValue));
		this.getFormHM().put("readonlyFilds", isNull(itemidarr));
		this.getFormHM().put("hideFilds", isNull(hideFlds));
		this.getFormHM().put("hideimgids", isNull(hidepics));
		this.getFormHM().put("hideSaveFlds", isNull(hideSaveFlds));
		this.getFormHM().put("isUnUmRela", isUnUmRela == null ? "false" : isUnUmRela);
		
		TrainExamPlanBo planBo = new TrainExamPlanBo(this.frameconn, this.userView);
		ArrayList examPapers = planBo.getExamPapers();
		this.getFormHM().put("examPapers", examPapers);

		String[] readonlyFilds = null;
		ArrayList readonlyFilds1 = new ArrayList();
		if (itemidarr != null && !"".equals(itemidarr))
		    readonlyFilds = itemidarr.split(",");

		if (readonlyFilds != null)
		    for (int j = 0; j < readonlyFilds.length; j++)
		    {
				String temp = readonlyFilds[j];
				if ("".equals(temp))
				    continue;
				LazyDynaBean abean = new LazyDynaBean();
				abean.set("itemid", temp);
				readonlyFilds1.add(abean);
		    }
		this.getFormHM().put("itemidarr", readonlyFilds1);

		String[] hidepics1 = null;
		ArrayList hidepics2 = new ArrayList();
		if (hidepics != null && !"".equals(hidepics))
		    hidepics1 = hidepics.split(",");

		if (hidepics1 != null)
		    for (int j = 0; j < hidepics1.length; j++)
		    {
				String temp = hidepics1[j];
				if ("".equals(temp))
				    continue;
				LazyDynaBean abean = new LazyDynaBean();
				abean.set("imgid", temp);
				hidepics2.add(abean);
		    }
		this.getFormHM().put("hidePics", hidepics2);

		TrainAddBo bo = new TrainAddBo("r54", this.frameconn);

		String b0110 = "";
		String e0122 = "";

		String primaryField = bo.getPrimaryField();
		HashMap initVals = bo.getInitValueMap(initValue);

		this.getFormHM().put("primaryField", primaryField);// 将主键的字段名存起来

		ContentDAO dao = new ContentDAO(this.getFrameconn());
		ArrayList fieldList = DataDictionary.getFieldList("R54", Constant.USED_FIELD_SET);
		ArrayList fieldInfoList = new ArrayList();
		try
		{
		    boolean isNew = false;
		    if (r5400 == null || "".equals(r5400))// 新建时候要生成主键
		    {
				this.getFormHM().put("chkflag", "add");
				
				isNew = true;
		    } 
		    else
			    this.getFormHM().put("chkflag", "edit");

		    this.getFormHM().put("r5400", SafeCode.encode(PubFunc.encrypt(r5400)));// 不管是新增还是修改，都将主键值存起来
		    for (int i = 0; i < fieldList.size(); i++)// 循环字段
		    {
				FieldItem fieldItem = (FieldItem) fieldList.get(i);
				String itemid = fieldItem.getItemid();
				String itemName = fieldItem.getItemdesc();
				String itemType = fieldItem.getItemtype();
				String codesetId = fieldItem.getCodesetid();
	
				if (hideFlds != null && hideFlds.length() > 1)
				{
				    if (hideFlds.indexOf(itemid) != -1)// 不显示的字段
				    	continue;
				}
	
				// 不显示编号字段（利用序号生成器产生的字段值）
				if (itemid.equalsIgnoreCase(primaryField))
				    continue;
				
				if ("r5401".equalsIgnoreCase(itemid))
					fieldItem.setFillable(true);
	
				FieldItemView fieldItemView = new FieldItemView();
				fieldItemView.setAuditingFormula(fieldItem.getAuditingFormula());
				fieldItemView.setAuditingInformation(fieldItem.getAuditingInformation());
				fieldItemView.setCodesetid(codesetId);
				fieldItemView.setDecimalwidth(fieldItem.getDecimalwidth());
				fieldItemView.setDisplayid(fieldItem.getDisplayid());
				fieldItemView.setDisplaywidth(fieldItem.getDisplaywidth());
				fieldItemView.setExplain(fieldItem.getExplain());
				fieldItemView.setFieldsetid(fieldItem.getFieldsetid());
				fieldItemView.setItemdesc(itemName);
				fieldItemView.setItemid(itemid);
				fieldItemView.setItemlength(fieldItem.getItemlength());
				fieldItemView.setItemtype(itemType);
				fieldItemView.setModuleflag(fieldItem.getModuleflag());
				fieldItemView.setState(fieldItem.getState());
				fieldItemView.setUseflag(fieldItem.getUseflag());
				fieldItemView.setPriv_status(fieldItem.getPriv_status());
				fieldItemView.setRowflag(String.valueOf(fieldList.size() - 1)); // 在struts用来表示换行的变量
				fieldItemView.setFillable(fieldItem.isFillable());//是否为必填项
	
				if (isNew)// 新建
				{
				    if (itemid.equals(primaryField))
				    {
				    	fieldItemView.setViewvalue(r5400);
				    	fieldItemView.setValue(r5400);
				    } 
				    else
				    {
						String temp = (String) initVals.get(itemid);// 按默认值进行初始化
						if (temp == null)
						{
						    fieldItemView.setViewvalue("");
						    fieldItemView.setValue("");
						} 
						else
						{
						    if ("0".equals(codesetId))
						    {
								fieldItemView.setViewvalue(temp);
								fieldItemView.setValue(temp);
						    } 
						    else
						    {
								fieldItemView.setViewvalue(AdminCode.getCode(codesetId, temp) != null ? AdminCode.getCode(codesetId, temp).getCodename() : "");
								fieldItemView.setValue(temp);
						    }
						}
				    }
				    
				    if ("b0110".equalsIgnoreCase(itemid))
				    {
						fieldItemView.setViewvalue(AdminCode.getCode("UN", b0110) != null ? AdminCode.getCode(codesetId, b0110).getCodename() : "");
						fieldItemView.setValue(b0110);
				    }				    
				} 
				else
				// 修改
				{
				    StringBuffer strsql = new StringBuffer();
				    strsql.append("select " + itemid + " from R54");
				    strsql.append(" where ");
				    strsql.append(primaryField + "='");
				    strsql.append(r5400 + "'");
		    	    strsql.append(" and r5400='"+r5400+"'");
	
				    this.frowset = dao.search(strsql.toString());
				    if (this.frowset.next())
				    {
						String value = "";
						if ("D".equals(itemType))
						{
						    Timestamp date = this.frowset.getTimestamp(itemid);
						    if (date != null)
								value = DateUtils.format(date, "yyyy-MM-dd HH:mm:ss");
						    else
								value = null;
						} 
						else
						{
						    value = this.frowset.getString(itemid);
						}
					
						if (value == null)
						{
						    fieldItemView.setViewvalue("");
						    fieldItemView.setValue("");
						} 
						else
						{
						    if ("A".equals(itemType) || "M".equals(itemType))
						    {
								if (!"0".equals(codesetId))
								{
								    String codevalue = value;
								    if (codevalue.trim().length() > 0 && codesetId != null && codesetId.trim().length() > 0)
								    	fieldItemView.setViewvalue(AdminCode.getCode(codesetId, codevalue) != null ? AdminCode.getCode(codesetId, codevalue).getCodename() : "");
								    else
										fieldItemView.setViewvalue("");
								    fieldItemView.setValue(value != null ? value.toString() : "");								    			
								} 
								else
								{
								    fieldItemView.setViewvalue(value);
								    fieldItemView.setValue(value);						
								}
						    } 
						    else if ("D".equals(itemType)) // 日期型有待格式化处理
						    {
								if ("r5405".equalsIgnoreCase(itemid))
								{
								    this.getFormHM().put("r5405_time", value.substring(11));
								}			
								else if ("r5406".equalsIgnoreCase(itemid)){
								    this.getFormHM().put("r5406_time", value.substring(11));
								}
		
								if (value != null && value.length() >= 10 && fieldItem.getItemlength() >= 10)
								{
								    value = new FormatValue().format(fieldItem, value.substring(0, 10));
								    value = PubFunc.replace(value, ".", "-");
								    fieldItemView.setViewvalue(value);
								    fieldItemView.setValue(value);
								} else if (value != null && value.toString().length() >= 10 && fieldItem.getItemlength() == 4)
								{
								    value = new FormatValue().format(fieldItem, value.substring(0, 4));
								    value = PubFunc.replace(value, ".", "-");
								    fieldItemView.setViewvalue(value);
								    fieldItemView.setValue(value);
								} else if (value != null && value.toString().length() >= 10 && fieldItem.getItemlength() == 7)
								{
								    value = new FormatValue().format(fieldItem, value.substring(0, 7));
								    value = PubFunc.replace(value, ".", "-");
								    fieldItemView.setViewvalue(value);
								    fieldItemView.setValue(value);
								} else
								{
								    fieldItemView.setViewvalue("");
								    fieldItemView.setValue("");
								}
						    } else
						    {
								// 数值类型的有待格式化处理
						    	value = PubFunc.DoFormatDecimal(value != null ? value.toString() : "", fieldItem.getDecimalwidth());
								fieldItemView.setValue(value);
								fieldItemView.setViewvalue(value);
						    }
						}
				    }
	
				}
				fieldInfoList.add(fieldItemView);

		    }
		    
		    fillParentCodeValue();

		} 
		catch (SQLException e)
		{
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		} 
		finally
		{
		    this.getFormHM().put("fieldlist", fieldInfoList);
		}
		
	}

    public String isNull(String str)
    {
		if (str == null)
		    return "";
		
		return str;
    }
    
    /**
     * 新增计划控制显示部门 业务用户先判断操作单位 无单位判断管理范围
     * 
     * @param 
     * @return
     */
	private void fillParentCodeValue() {
        String temp="";
        try {
            if(!userView.isSuper_admin()){
                TrainCourseBo bo =  new TrainCourseBo(this.userView);
                temp=bo.getUnitIdByBusi();//this.userView.getUnitIdByBusi("6");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.getFormHM().put("orgparentcode",temp);
	}
}
