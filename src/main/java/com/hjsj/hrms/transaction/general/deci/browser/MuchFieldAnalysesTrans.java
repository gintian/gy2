package com.hjsj.hrms.transaction.general.deci.browser;

import com.hjsj.hrms.businessobject.general.deci.browser.MuchFieldAnalyse;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.interfaces.sys.chartset.ChartParameterXML;
import com.hjsj.hrms.taglib.general.ChartParameter;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class MuchFieldAnalysesTrans extends IBusiness {

	public void execute() throws GeneralException {
		//指标ID
		String itemid = (String)(((HashMap)(this.getFormHM().get("requestPamaHM"))).get("itemid")) ;	
		
		String dbflag = "";		//库标识
		String itemName = "";   //图表显示名称
		
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		if(itemid == null || "".equals(itemid)){
		}else{
			String sql = "select * from ds_key_item where itemid ='"+ itemid +"'";
		    try {
				this.frowset = dao.search(sql);
				if(this.frowset.next()){
				    dbflag = this.frowset.getString("flag"); // 标识（人员 单位  职位）	
				    itemName = this.frowset.getString("itemname");
				}
			} catch (SQLException e) {
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}
		}
		
		/*//判断要分析的代码指标是否按月变化
		String changeFalg = "";
		String sql = "select changeFlag from fieldset where fieldsetid=( " +
				"select fieldsetid from fielditem where itemid='"+ field_name + "')";
		RowSet rs = null;
		try {
			rs = dao.search(sql);
			if (rs.next()) {
				changeFalg = rs.getString("changeflag");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}*/
		
		//如果是人员则要选择人员库
		String dbpre = ""; //默认人员库前缀
		if("A".equals(dbflag)){
			//授权当前用户的应用库列表
			ArrayList dblist=this.userView.getPrivDbList();
			DbNameBo dbvo=new DbNameBo(this.getFrameconn());
			dblist=dbvo.getDbNameVoList(dblist);
			ArrayList list=new ArrayList();
			for(int i=0;i<dblist.size();i++)
			{
				CommonData vo=new CommonData();
				RecordVo dbname=(RecordVo)dblist.get(i);
				vo.setDataName(dbname.getString("dbname"));
				vo.setDataValue(dbname.getString("pre"));
				list.add(vo);
			}
			this.getFormHM().put("dblist",list);
			CommonData v = (CommonData)list.get(0);
			dbpre = v.getDataValue();
		}

		//单击树时自动分析
		MuchFieldAnalyse mfa = new MuchFieldAnalyse(this.getFrameconn(),itemid);
		
		String changeFlag = mfa.getChangeFlag();//分析数据的按年/按月标识

		//System.out.println("按年按月变化标识=" + changeFlag);
		
		this.getFormHM().put("changeFlagValue",changeFlag);
		
		HashMap chartMap = new HashMap();
		if("0".equals(changeFlag)){//不按月变化
			this.formHM.put("changeFlag","no");			
			if("A".equals(dbflag)){
				chartMap = mfa.muchFieldAnalyse("1",dbflag,dbpre,changeFlag,"","","","");
			}else{
				chartMap = mfa.muchFieldAnalyse("1",dbflag,"",changeFlag,"","","","");
			}
				
		}else{
			this.formHM.put("changeFlag","yes");
			Calendar c = Calendar.getInstance();
			String year = String.valueOf(c.get(Calendar.YEAR));
			String month = String.valueOf(c.get(Calendar.MONTH)+1);
			if("1".equals(changeFlag)){//按月
				if("A".equals(dbflag)){
					chartMap = mfa.muchFieldAnalyse("1",dbflag,dbpre,changeFlag,year,month,"","");
				}else{
					chartMap = mfa.muchFieldAnalyse("1",dbflag,"",changeFlag,year,month,"","");
				}
			}else if("2".equals(changeFlag)){//按年
				if("A".equals(dbflag)){
					chartMap = mfa.muchFieldAnalyse("1",dbflag,dbpre,changeFlag,year,"","","");
				}else{
					chartMap = mfa.muchFieldAnalyse("1",dbflag,"",changeFlag,year,"","","");
				}
			}
			
		
		}
		
		this.getFormHM().put("itemName",itemName);
		this.getFormHM().put("analyseType","1");
		this.getFormHM().put("chartFlag","yes");
		this.getFormHM().put("chartMap",chartMap);
		this.getFormHM().put("chartTitle",itemName);
		this.getFormHM().put("chartType","4");
		this.formHM.put("itemid",itemid);    //指标ID
		this.formHM.put("dbflag",dbflag);    //库标志	
		
		//获取图例配置参数信息（如果用户设置则依据用户设置显示/如果用户没有设置则系统默认形式显示）
		ChartParameterXML cpxml = new ChartParameterXML(this.getFrameconn());
		ChartParameter cp = cpxml.searchChartParameter(userView.getUserName());
		cp.setItemLabelsVisible(true);
		cp.setChartTitle(itemName);

		//控制网页对话框信息显示
		String chartsets = ChartParameter.analyseChartParameter(cp);
		this.getFormHM().put("chartsets",chartsets);
		this.getFormHM().put("chartParameters",cp);
	}


}
