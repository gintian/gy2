/**
 * 
 */
package com.hjsj.hrms.transaction.general.deci.browser;

import com.hjsj.hrms.businessobject.general.deci.browser.SingleFieldAnalyse;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.interfaces.sys.chartset.ChartParameterXML;
import com.hjsj.hrms.taglib.general.ChartParameter;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * <p> Title:SingleFieldAnalyseTrans </p>
 * <p> Description:单指标分析 </p>
 * <p> Company:hjsj </p>
 * <p> create time:Aug 29, 2006:1:48:05 PM </p>
 * 
 * @author zhangfengjin
 * @version 1.0
 * 
 */
public class SingleFieldAnalysesTrans extends IBusiness {

	public void execute() throws GeneralException {
		//要指标分析的序号
		String factorid = (String) (((HashMap) (this.getFormHM().get("requestPamaHM"))).get("factorid"));
		
		String dbflag = ""; // 库标识（A B K）
		String chartTitle = "";	//图形显示描述信息(标题)
		String formulas = ""; //公式信息
		float standard_value = 0; //标准值
		float control_value= 0;//控制值
		
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		if (factorid == null || "".equals(factorid)) {
			//错误
		} else {
			String sql = "select * from ds_key_factor where factorid ='"+ factorid + "'";
			try {
				this.frowset = dao.search(sql);
				if (this.frowset.next()) {
					dbflag = this.frowset.getString("flag"); // 标识（人员 单位 职位）
					chartTitle = this.frowset.getString("name");
					formulas = this.frowset.getString("formula");
					standard_value=this.frowset.getFloat("standard_value");
					control_value=this.frowset.getFloat("control_value");	
				}
			} catch (SQLException e) {
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}
		}	

		String dbpre = ""; //默认人员库前缀
		if ("A".equals(dbflag)) {
			// 授权当前用户的应用库前缀列表
			ArrayList dblist = this.userView.getPrivDbList();
			DbNameBo dbvo = new DbNameBo(this.getFrameconn());
			dblist = dbvo.getDbNameVoList(dblist);
			ArrayList list = new ArrayList();
			for (int i = 0; i < dblist.size(); i++) {
				CommonData vo = new CommonData();
				RecordVo dbname = (RecordVo) dblist.get(i);
				vo.setDataName(dbname.getString("dbname"));
				vo.setDataValue(dbname.getString("pre"));
				list.add(vo);
			}
			
			this.getFormHM().put("dblist", list);
			
			CommonData v = (CommonData)list.get(0);
			dbpre = v.getDataValue();
		}

		SingleFieldAnalyse sfa = new SingleFieldAnalyse(this.getFrameconn(),factorid);
		
		//分析公式是否按月/年变化
		String cfs = "";
		ArrayList formulaList = sfa.getFormulaList(formulas);
		for(int i=0; i<formulaList.size();i++){
			String temp = this.getChangFlag((String)formulaList.get(i));
			cfs+=temp;
		}
		String changeFlag = "";
		if(cfs.indexOf("1")!= -1){//月
			changeFlag ="1";
		}else if(cfs.indexOf("2")!= -1){//年
			changeFlag = "2";
		}else{ //一般
			changeFlag="0";
		}
		
		//System.out.println("最终按年/月变化标识="+changeFlag);
		
		ArrayList chartList = new ArrayList();
		if("0".equals(changeFlag)){//一般
			this.formHM.put("changeFlag","no");
			if("A".equals(dbflag)){
				chartList = (ArrayList) sfa.singleFieldAnalyse("1",dbflag,dbpre,"","","","");
			}else{
				chartList = (ArrayList) sfa.singleFieldAnalyse("1",dbflag,"","","","","");
			}
		}else{
			this.formHM.put("changeFlag","yes");
			Calendar c = Calendar.getInstance();
			String year = String.valueOf(c.get(Calendar.YEAR));
			String month = String.valueOf(c.get(Calendar.MONTH)+1);
			if("1".equals(changeFlag)){//按月变化
				if("A".equals(dbflag)){
					chartList = (ArrayList) sfa.singleFieldAnalyse("1",dbflag,dbpre,year,month,"","");
				}else{
					chartList = (ArrayList) sfa.singleFieldAnalyse("1",dbflag,"",year,month,"","");
				}
			}else if("2".equals(changeFlag)){//按年变化
				if("A".equals(dbflag)){
					chartList = (ArrayList) sfa.singleFieldAnalyse("1",dbflag,dbpre,year,"",year,"");
				}else{
					chartList = (ArrayList) sfa.singleFieldAnalyse("1",dbflag,"",year,"",year,"");
				}
			}
		}
		
		this.getFormHM().put("changeFlagValue",changeFlag);
		this.getFormHM().put("analyseType","1");
		this.getFormHM().put("chartFlag","yes");
		this.getFormHM().put("chartList",chartList);
		this.getFormHM().put("chartTitle",chartTitle);
		this.getFormHM().put("chartType","11");

		this.formHM.put("factorid", factorid); // 指标ID
		this.formHM.put("dbflag", dbflag); // 库标志
		
		String av = sfa.getAvgValue();
		double avgValue=0;
		if(av==null || "".equals(av)){
		}else{
			avgValue = Double.parseDouble(sfa.getAvgValue());
		}
		
		StringBuffer controlStr= new StringBuffer();
		HashMap map = new HashMap();
		if(avgValue != 0){
			map.put("平均值",String.valueOf(avgValue));
			controlStr.append("平均值,");
		}
		if(standard_value !=0){
			map.put("标准值",String.valueOf(standard_value));
			controlStr.append("标准值,");
		}
		if(control_value!=0){
			map.put("控制值",String.valueOf(control_value));
			controlStr.append("控制值,");
		}
		if(controlStr == null || "".equals(controlStr.toString())){
			controlStr.append("");
		}else{
			controlStr.deleteCharAt(controlStr.length()-1);			
		}

		//获取图例配置参数信息（如果用户设置则依据用户设置显示/如果用户没有设置则系统默认形式显示）
		ChartParameterXML cpxml = new ChartParameterXML(this.getFrameconn());
		ChartParameter cp = cpxml.searchChartParameter(userView.getUserName());
		cp.setMarkerMap(map);
		cp.setItemLabelsVisible(true);
		cp.setChartTitle(chartTitle);

		//控制网页对话框信息显示
		String chartsets = ChartParameter.analyseChartParameter(cp);
		this.getFormHM().put("chartsets",chartsets);
		this.getFormHM().put("chartParameters",cp);
		this.getFormHM().put("controlStr",controlStr.toString());//图形参数设置用
	}

	/**
	 * 获得公式的年月变化标识
	 * @param fieldItem
	 * @return 0,一般子集 1,按月变化 2,按年变化
	 * @throws GeneralException
	 */
	public String getChangFlag(String itemid) throws GeneralException{
		String changeFlag = "";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String sql = "select changeFlag from fieldset where fieldsetid=( " +
				"select fieldsetid from fielditem where itemid='"+ itemid + "')";
		
		/*System.out.println("********************************");
		System.out.println(sql);
		System.out.println("*********************************");*/
		
		RowSet rs = null;
		try {
			rs = dao.search(sql);
			if (rs.next()) {
				changeFlag = rs.getString("changeflag");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		if(changeFlag == null|| "".equals(changeFlag)){
			changeFlag ="0";
		}
		return changeFlag;
	}
	
}
