package com.hjsj.hrms.transaction.train.plan;

import com.hjsj.hrms.businessobject.performance.singleGrade.TableOperateBo;
import com.hjsj.hrms.businessobject.train.CtrlParamXmlBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.Iterator;

public class GetTrainClassInfoTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String r3101=(String)this.getFormHM().get("r3101");
			// 1: 活动评估结果   2:教师评估结果 3:活动效果调查问卷结果  4:教师教学效果调查问卷结果
			String opt=(String)this.getFormHM().get("opt"); 
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			
			String templateid="";
			String id="";
			
			
			RecordVo vo=new RecordVo("r31");
			if(!vo.hasAttribute("ctrl_param"))
			{
				DbWizard dbWizard=new DbWizard(this.getFrameconn());
				DBMetaModel dbmodel=new DBMetaModel(this.getFrameconn());
				
				Table table=new Table("r31");
				TableOperateBo tableOperateBo=new TableOperateBo(this.getFrameconn());
				Field aField0=tableOperateBo.getField(false,"ctrl_param",ResourceFactory.getProperty("train.plan.control.parameters"),"M",3,0);
				table.addField(aField0);
				dbWizard.addColumns(table);
				dbmodel.reloadTableModel("r31");
			}
			
			this.frowset=dao.search("select r3130,ctrl_param,r31.r3101 from r31 where r3101='"+r3101+"'");
			CtrlParamXmlBo xmlBo=new CtrlParamXmlBo(this.getFrameconn());
			String a_name="";
			if(this.frowset.next())
			{
				String ctrl_param=Sql_switcher.readMemo(this.frowset,"ctrl_param");
				
				if(this.frowset.getString("r3130")!=null)
					a_name=this.frowset.getString("r3130");
				if(ctrl_param!=null&&ctrl_param.trim().length()>0)
				{
					xmlBo.setXml(ctrl_param);
					ArrayList lists=xmlBo.getEvaluateModelList();
					for(Iterator a=lists.iterator();a.hasNext();)
					{
						LazyDynaBean abean=(LazyDynaBean)a.next();
						String name=(String)abean.get("name");   //  1:评估模版  0：调查问卷
						String type=(String)abean.get("type");
						String run=(String)abean.get("run");
						String end_date=(String)abean.get("end_date");
						String value=(String)abean.get("value");

						if("job".equals(type))
						{
									if("1".equals(name)&& "1".equals(opt))
									{
										templateid=value;
									}
									else if("0".equals(name)&& "3".equals(opt))
									{
										id=value;
									}
						}
						else
						{
									if("1".equals(name)&& "2".equals(opt))
										templateid=value;
									else if("0".equals(name)&& "4".equals(opt))
										id=value;
						}
								
					}
				}
			}
			
			if("1".equals(opt)|| "2".equals(opt))
			{
				if(templateid.trim().length()==0)
				{
					throw GeneralExceptionHandler.Handle(new Exception(a_name+ResourceFactory.getProperty("train.plan.noset.assessment.moban")+"！"));
				}
			}
			else
			{
				if(id.trim().length()==0)
				{
					throw GeneralExceptionHandler.Handle(new Exception(a_name+ResourceFactory.getProperty("train.plan.noset.survey")+"！"));
				}
			}
			this.getFormHM().put("templateid",templateid);
			this.getFormHM().put("id",id);
			this.getFormHM().put("mdid",PubFunc.encryption(id));
			this.getFormHM().put("r3101",r3101);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

}
