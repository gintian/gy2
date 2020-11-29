package com.hjsj.hrms.businessobject.sys;

import com.hjsj.hrms.businessobject.competencymodal.personPostModal.PersonPostModalBo;
import com.hjsj.hrms.businessobject.hire.EmployNetPortalBo;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.businessobject.performance.batchGrade.AnalysePlanParameterBo;
import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeBo;
import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeSinglePointBo;
import com.hjsj.hrms.businessobject.performance.batchGrade.PerformanceConstantBo;
import com.hjsj.hrms.businessobject.sys.cms.Cms_ChannelBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.gz.utils.SalaryCtrlParamBo;
import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterSetBo;
import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterXMLBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.dom4j.Attribute;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.jdom.Document;
import org.jdom.Element;
import org.slf4j.LoggerFactory;

import javax.sql.RowSet;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

public class LoadDynamicParametersBo {
	Connection conn;
	static org.slf4j.Logger log = LoggerFactory.getLogger(PubFunc.class);
	public LoadDynamicParametersBo(Connection conn)
	{
		this.conn = conn;
	}
	
	/**
	 * 重新加载系统内各模块参数
	 * @Title: reloadAllParam   
	 * @Description: 各模块中有需要动态加载的参数都在此处自行添加，
	 * 注意TryCatch，避免异常导致后续操作中断
	 */
	public void reloadAllParam() {
	    /**加载绩效考核参数*/
        this.relovatePerformanceStaticParam();
		//日志热切换
		Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(conn);
		String loglevel=sysbo.getValue(Sys_Oth_Parameter.LOGLEVEL);
		Logger logger = LogManager.getRootLogger();
		String log4jlevel = String.valueOf(logger.getLevel());
		log.info("loglevel:{} log4jlevel:{}",loglevel,log4jlevel);
		if(org.apache.commons.lang3.StringUtils.isEmpty(loglevel)||!loglevel.equalsIgnoreCase(log4jlevel)){
			updateLevel(loglevel);
		}
        //加载招聘参数及外网内容发布
        try {
            /** 加载招聘单层代码到静态变量*/
            EmployNetPortalBo.codeSetMap = null;
            /**动态加载招聘参数*/
            ParameterXMLBo pb = new ParameterXMLBo(this.conn);
            ParameterXMLBo.hm = null;
            pb.getAttributeValues();
            ParameterSetBo.prompt_content = null;
            ParameterSetBo.license_agreement = null;
            ParameterSetBo.cultureList = null;
            
            EmployNetPortalBo bo = new EmployNetPortalBo(this.conn);
            bo.refreshStaticAttribute();
            
            //招聘外网重新加载内容平台
            Cms_ChannelBo cms_bo=new Cms_ChannelBo(this.conn);
            cms_bo.refreshChildlist();
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        /** 加载薪资控制参数静态变量*/        
        try {
            SalaryCtrlParamBo salaryCtrlParamBo =new SalaryCtrlParamBo();
            salaryCtrlParamBo.setConn(this.conn);
            salaryCtrlParamBo.docMap = new HashMap();
            salaryCtrlParamBo.initAllSalaryCtrlParam();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        //加载考勤参数
        try {
            KqParam.getInstance().reloadAllParam();
        } catch (Exception e) {
            e.printStackTrace();
        } 
	}
	/**
	 * 修改Logger的日志级别
	 * @param level
	 */
	public static void updateLevel(String level){
		FileOutputStream out = null;
		try{
			if(org.apache.commons.lang3.StringUtils.isNotEmpty(level)){
				String path = Thread.currentThread().getContextClassLoader().getResource("/").getPath()+"log4j2.xml";
				//1.创建并读取一个Document对象
				org.dom4j.Document doc=new SAXReader().read(new File(path));
				org.dom4j.Element contatcElem = doc.getRootElement().element("Loggers").element("root");
				Attribute levelAttribute= contatcElem.attribute("level");
				levelAttribute.setValue(level);
				//1.创建输出流通道
				out=new FileOutputStream(path);
				OutputFormat format=OutputFormat.createPrettyPrint();//设置contact.xml文件格式（俗称：美观格式）
				format.setEncoding("UTF-8");//设置编码格式
				//2.创建写出的对象
				XMLWriter write=new XMLWriter(out,format);
				//3.写出对象
				write.write(doc);
				//4.关闭资源
				write.close();
/*				// 获取Logger对象，修改Logger对象的Level
				Logger logger = LogManager.getRootLogger();
				System.out.println("改变前："+logger.getLevel());
				logger.setLevel(Level.toLevel(level));
				System.out.println("改变后："+logger.getLevel());*/
			}
		}catch (Exception e){
			e.printStackTrace();
		}finally {
			PubFunc.closeResource(out);
		}

	}
	/**
	 * 刷新绩效静态变量 参数
	 *
	 */
	private void relovatePerformanceStaticParam()
	{
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			RowSet     frowset=null;
			frowset=dao.search("select plan_id,parameter_content,template_id,method from per_plan  where status not in ( 0, 1 , 2,  3 , 5 , 7)");
			LoadXml loadXml=null;
			
		 	PerformanceConstantBo.pointGradeDesc_templateMap=new HashMap();
		 	PerformanceConstantBo.pointList_templateMap=new HashMap();
		 	BatchGradeSinglePointBo singlePointBo=new BatchGradeSinglePointBo(this.conn);
		 	
			while(frowset.next())
			{
				int method=frowset.getInt("method");
				String plan_id=frowset.getString("plan_id");
				String xmlContext = Sql_switcher.readMemo(frowset, "parameter_content");
				String template_id=frowset.getString("template_id");
				loadXml=new LoadXml(this.conn,xmlContext,1);
				Hashtable htxml =loadXml.getDegreeWhole();
				String showOneMark = (String) htxml.get("ShowOneMark"); // BS打分时显示统一打分的指标，以便参考
				
				BatchGradeBo.getPlanLoadXmlMap().put(plan_id,loadXml);
				getPerPointList(template_id,plan_id,showOneMark,method);
				getPerPointList2(template_id,plan_id,showOneMark,method);
				
				String pointEvalType=(String)htxml.get("PointEvalType");
	   			String MutiScoreOnePageOnePoint=(String)htxml.get("MutiScoreOnePageOnePoint");
				
				//潍柴 单指标多人打分
	   			if((SystemConfig.getPropertyValue("batchgrade_radiotype")!=null && "multiple".equalsIgnoreCase(SystemConfig.getPropertyValue("batchgrade_radiotype").trim())) ||( "1".equals(pointEvalType)&& "True".equalsIgnoreCase(MutiScoreOnePageOnePoint) ))
				{
					PerformanceConstantBo.pointGradeDesc_templateMap.put(template_id,singlePointBo.getPointGradeDescMap(template_id));
					PerformanceConstantBo.pointList_templateMap.put(template_id,singlePointBo.getPointList(template_id));
				}
			}
		//	if(SystemConfig.getPropertyValue("batchgrade_radiotype")!=null&&SystemConfig.getPropertyValue("batchgrade_radiotype").equalsIgnoreCase("multiple"))
				PerformanceConstantBo.standPointGradeDescList=singlePointBo.getStandPointGradeDescList();
			AnalysePlanParameterBo bo=new AnalysePlanParameterBo(this.conn);
			
			frowset=dao.search("select str_value from constant where constant='PER_PARAMETERS'");
			if(frowset.next())
			{
				String values=Sql_switcher.readMemo(frowset,"str_value");
				if(!"".equals(values.trim()))
				{
					Element root;
					try
					{
							Document doc = PubFunc.generateDom(values);
							root = doc.getRootElement();
							Hashtable tempHash = new Hashtable();
							tempHash=bo.getElements(root);
							AnalysePlanParameterBo.setReturnHt(tempHash);
							
						} catch (Exception ex) 
						{
								ex.printStackTrace();
						}
					}

				}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	  private void getPerPointList2(String templateID, String plan_id,String showOneMark,int method)
	  {

		  		ArrayList list = new ArrayList();
	    	
			
				HashMap pointGradeMap = new HashMap();
				ArrayList a_pointGrageList = new ArrayList();
				ArrayList pointList = new ArrayList();
				ContentDAO dao = new ContentDAO(this.conn);
				RowSet rowSet = null;
			
				PersonPostModalBo ppo = new PersonPostModalBo(this.conn);
				String per_comTable = "per_grade_template"; // 绩效标准标度
				if(ppo.getComOrPer(templateID,"temp")) {
                    per_comTable = "per_grade_competence"; // 能力素质标准标度
                }
				HashMap map2 = new HashMap();
				String sql = "select pp.item_id,po.point_id,po.pointname,po.pointkind,pgt.gradedesc,pg.gradecode,pg.top_value,pg.bottom_value,pp.score,pg.gradevalue,po.fielditem,po.l_fielditem  from per_template_item pi,per_template_point pp,per_point po ,per_grade pg,"+per_comTable+" pgt "
					+ " where pi.item_id=pp.item_id and pp.point_id=po.point_id and  po.point_id=pg.point_id and pg.gradecode=pgt.grade_template_id and template_id='" + templateID + "'  "; // pi.seq,
				if ("false".equalsIgnoreCase(showOneMark) && method!= 2) //  2010/10/29  dengcan
                {
                    sql +=" and (( po.pointkind='1' and ( po.status<>1 or po.status is null )  ) or po.pointkind='0'  )  ";
                }
				sql+="  order by pp.seq"; // pi.seq,
				try
				{
				    rowSet = dao.search(sql);
				    while (rowSet.next())
				    {
					String[] temp = new String[12];
					for (int i = 0; i < 12; i++)
					{
					    if (i == 2) {
                            temp[i] = Sql_switcher.readMemo(rowSet, "pointname");
                        } else if (i == 4) {
                            temp[i] = Sql_switcher.readMemo(rowSet, "gradedesc");
                        } else {
                            temp[i] = rowSet.getString(i + 1);
                        }
			
					}
					a_pointGrageList.add(temp);
				    }
				    
				    sql="select po.point_id,po.pointname,po.pointkind,pi.item_id,po.visible,po.fielditem,po.l_fielditem,po.status from per_template_item pi,per_template_point pp,per_point po "
					    + " where pi.item_id=pp.item_id and pp.point_id=po.point_id  and template_id='" + templateID + "'  ";
					if ("false".equalsIgnoreCase(showOneMark) && method != 2) //  2010/10/29  dengcan
                    {
                        sql +=" and (( po.pointkind='1' and ( po.status<>1 or po.status is null )  ) or po.pointkind='0'  )  ";
                    }
				    sql+= "   order by pp.seq";
				    rowSet = dao
					    .search(sql); // pi.seq,
				    
				    while (rowSet.next())
				    {
					String[] temp = new String[9];
					temp[0] = rowSet.getString(1);
					temp[1] = Sql_switcher.readMemo(rowSet, "pointname");
					temp[2] = rowSet.getString(3);
					temp[3] = rowSet.getString(4);
					temp[4] = "";
					temp[5] = rowSet.getString("visible");
					temp[6] = rowSet.getString("fielditem");
					temp[7] = rowSet.getString("l_fielditem");
					temp[8] = rowSet.getString("status");
					map2.put(temp[0].toLowerCase(), temp);
				    }
				    // 解决排列顺序问题
				    ArrayList seqList = new ArrayList();
				    rowSet = dao.search("select * from per_result_" + plan_id);
				    ResultSetMetaData metadata = rowSet.getMetaData();
				    int i = 1;
				    while (i <= metadata.getColumnCount())
				    {
					String tempName = metadata.getColumnName(i);
					if ("C_".equals(tempName.substring(0, 2)))
					{
					    seqList.add(tempName.substring(2).toLowerCase());
			
					}
					i++;
				    }
			
				    for (int a = 0; a < seqList.size(); a++)
				    { 
						String temp = (String) seqList.get(a);
						if( map2.get(temp)!=null) {
                            pointList.add((String[]) map2.get(temp));
                        }
				    }
			
				    for (int j = 0; j < seqList.size(); j++)
				    {
					String temp = (String) seqList.get(j);
					ArrayList tempList = new ArrayList();
					int a = 0;
					for (int z = 0; z < a_pointGrageList.size(); z++)
					{
					    String[] tt = (String[]) a_pointGrageList.get(z);
					    if (tt[1].toLowerCase().equals(temp))
					    {
						tempList.add(tt);
						a++;
					    } else if (a != 0 && !tt[1].toLowerCase().equals(temp))
					    {
						break;
					    }
					}
					pointGradeMap.put(temp, tempList);
			
				    }
			
				} catch (Exception e)
				{
				    e.printStackTrace();
			
				}
				list.add(pointList);
				list.add(pointGradeMap);
				
				BatchGradeBo.getPlan_perPointMap2().put(plan_id, list);
	    	
	    }
	
	  /**
     * 返回 某绩效模版的所有指标集
     * 
     * @param templateID
     * @return
     */
	public void getPerPointList(String templateID, String plan_id,String showOneMark,int method) throws GeneralException
	{
		ArrayList list = new ArrayList();
		BatchGradeBo bo=new BatchGradeBo(this.conn);
	
		ArrayList pointGrageList = new ArrayList();
		ArrayList a_pointGrageList = new ArrayList();
		ArrayList pointList = new ArrayList();
		ContentDAO dao = new ContentDAO(this.conn);
		String isNull = "0"; // 判断模版中指标标度上下限值是否设置
		RowSet rowSet = null;
		StringBuffer noGradeItem = new StringBuffer(",");
	
		PersonPostModalBo ppo = new PersonPostModalBo(this.conn);
		String per_comTable = "per_grade_template"; // 绩效标准标度
		if(ppo.getComOrPer(templateID,"temp")) {
            per_comTable = "per_grade_competence"; // 能力素质标准标度
        }
		HashMap map2 = new HashMap();
		String sql = "select pp.item_id,po.point_id,po.pointname,po.pointkind,pg.gradedesc,pg.gradecode,pg.top_value,pg.bottom_value,pp.score,pg.gradevalue,po.fielditem,po.l_fielditem,po.status,pgt.gradedesc  from per_template_item pi,per_template_point pp,per_point po ,per_grade pg,"+per_comTable+" pgt "
			+ " where pi.item_id=pp.item_id and pp.point_id=po.point_id and  po.point_id=pg.point_id  and pg.gradecode=pgt.grade_template_id   and template_id='" + templateID + "' "; // pi.seq,
		
		if (showOneMark!=null&& "false".equalsIgnoreCase(showOneMark) &&method != 2) //  2010/10/29  dengcan
        {
            sql +=" and (( po.pointkind='1' and ( po.status<>1 or po.status is null )  ) or po.pointkind='0'  )  ";
        }
		
		sql += "  order by pp.seq,pg.gradecode";
		try
		{
		    HashMap map = new HashMap();
		    rowSet = dao.search(sql);
		    while (rowSet.next())
		    {
			String[] temp = new String[14];
			for (int i = 0; i < 14; i++)
			{
			    if (i == 2) {
                    temp[i] = Sql_switcher.readMemo(rowSet, "pointname");
                } else if (i == 4) {
                    temp[i] = Sql_switcher.readMemo(rowSet, "gradedesc");
                } else {
                    temp[i] = rowSet.getString(i + 1);
                }
			    if (i == 6 || i == 7)
			    {
				if (temp[i] == null)
				{
				    isNull = "1";
				    if (map.get(rowSet.getString("point_id")) == null)
				    {
					noGradeItem.append(rowSet.getString("point_id") + ",");
					map.put(rowSet.getString("point_id"), "1");
				    }
				}
			    }
	
			}
			a_pointGrageList.add(temp);
		    }
		    sql = "select po.point_id,po.pointname,po.pointkind,pi.item_id,po.visible,po.fielditem,po.status,pp.score,po.status,pp.score,po.Kh_content,po.Gd_principle,po.Description from per_template_item pi,per_template_point pp,per_point po "
			    + " where pi.item_id=pp.item_id and pp.point_id=po.point_id  and template_id='" + templateID + "' "; // pi.seq,
		    if ("false".equalsIgnoreCase(showOneMark) &&method != 2) //  2010/10/29  dengcan
            {
                sql +=" and (( po.pointkind='1' and ( po.status<>1 or po.status is null )  ) or po.pointkind='0'  )  ";
            }
		    sql += " order by pp.seq";
		    rowSet = dao.search(sql);
	
		    // 解决排列顺序问题
		    ArrayList seqList = new ArrayList();
		    ArrayList tempPointList=new ArrayList();
		    while (rowSet.next())
		    {
				String[] temp = new String[12];
				temp[0] = rowSet.getString(1);
				temp[1] = Sql_switcher.readMemo(rowSet, "pointname");
				temp[2] = rowSet.getString(3);
				temp[3] = rowSet.getString(4);
				temp[4] = "";
				temp[5] = rowSet.getString("visible");
				temp[6] = rowSet.getString("fielditem");
				temp[7] = rowSet.getString("status") != null ? rowSet.getString("status") : "0";
				temp[8] = rowSet.getString("score");
		
				temp[9] = Sql_switcher.readMemo(rowSet, "kh_content");
				temp[10] = Sql_switcher.readMemo(rowSet, "gd_principle");		
				temp[11]=Sql_switcher.readMemo(rowSet, "description"); 
				tempPointList.add(temp);
				map2.put(temp[0].toLowerCase(), temp);
		    }
		    //这段代码不能去掉啦，否则会影响打分界面的显示问题－－－－dengcan-2009-6-8------
		    bo.get_LeafItemList(templateID,tempPointList ,seqList);	   
		    for (Iterator t = seqList.iterator(); t.hasNext();)
		    {
				String temp = (String) t.next();
				String[] atemp = (String[]) map2.get(temp);
				pointList.add(atemp);
		    }
	
		    for (Iterator t = seqList.iterator(); t.hasNext();)
		    {
			String temp = (String) t.next();
			for (Iterator t1 = a_pointGrageList.iterator(); t1.hasNext();)
			{
			    String[] tt = (String[]) t1.next();
			    if (tt[1].toLowerCase().equals(temp)) {
                    pointGrageList.add(tt);
                }
			}
		    }
	
		} catch (Exception e)
		{
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
		list.add(pointGrageList);
		list.add(pointList);
		list.add(isNull);
		list.add(noGradeItem.toString());
		BatchGradeBo.getPlan_perPointMap().put(plan_id,list);
	}
	

}
