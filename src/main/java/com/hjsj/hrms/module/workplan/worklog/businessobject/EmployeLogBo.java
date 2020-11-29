package com.hjsj.hrms.module.workplan.worklog.businessobject;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.workplan.config.businessobject.WorkPlanConfigBo;
import com.hjsj.hrms.utils.components.tablefactory.businessobject.TableFactoryBO;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnConfig;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;

import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class EmployeLogBo {
	private UserView userview;
	private Connection conn;
	
	public EmployeLogBo(UserView userview, Connection conn) {
		this.userview = userview;
		this.conn = conn;
	}
	/***
	 *
	 * */
	public ArrayList<ColumnsInfo> getColumnList(){
        ArrayList<ColumnsInfo> infolist=new ArrayList<ColumnsInfo>();
        TableFactoryBO tableBo = new TableFactoryBO("employlog_00001", this.userview, conn);
        HashMap scheme = tableBo.getTableLayoutConfig();
        /*
         *   员工日志监控加上了“增加主集支持”，
         *   但是之前的代码应该是不对的，此处暂时按照我自己的想法改了，因为没找到是谁写的这个功能。
         *   PS：改为在栏目设置添加哪里可以添加主集指标，不选的话默认不选进来主集指标
         *   haosl 2019年4月16日
         */
        if (scheme != null) {
            Integer schemeId = (Integer) scheme.get("schemeId");
            ArrayList<ColumnConfig> columnConfigList = tableBo.getTableColumnConfig(schemeId);
            String mergeDesc = "";
            ArrayList<ColumnsInfo> mergeColumns = new ArrayList<ColumnsInfo>();
            Boolean mergeLockState = null;
            int mergeIndex = 0;
            for(int i = 0; i < columnConfigList.size(); i++){
                ColumnConfig column = columnConfigList.get(i);
                if(null == column)
                    continue;
                FieldItem fi = null;
                if(StringUtils.isNotBlank(column.getFieldsetid())) {
                    fi = DataDictionary.getFieldItem(column.getItemid(), column.getFieldsetid());
                }else{
                    fi = DataDictionary.getFieldItem(column.getItemid(), "A01");
                }
                String columnId = column.getItemid();
                ColumnsInfo info = new ColumnsInfo();
                info.setColumnId(columnId);
                info.setFilterable(false);
                info.setEditableValidFunc("false");
                if("1".equals(column.getIs_display())){
                    info.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
                }else{
                    //如果此列为 总是加载数据列，设置为只加载数据
                    if(info.getLoadtype()==ColumnsInfo.LOADTYPE_ALWAYSLOAD || info.getLoadtype()==ColumnsInfo.LOADTYPE_ALWAYSLOAD_HIDE)
                        info.setLoadtype(ColumnsInfo.LOADTYPE_ALWAYSLOAD_HIDE);
                    else
                        info.setLoadtype(ColumnsInfo.LOADTYPE_NOTLOAD);

                    if(column.getMergedesc()==null || column.getMergedesc().length()==0)
                        column.setMergedesc(mergeDesc);
                }
                if("a0101".equalsIgnoreCase(columnId)){
                    info.setRendererFunc("employeLog_me.showWorkLog");
                }
                //数据布局  左对齐、居中、 右对齐
                switch(column.getAlign()){
                    case 1:
                        info.setTextAlign("left");
                        break;
                    case 2:
                        info.setTextAlign("center");
                        break;
                    case 3:
                        info.setTextAlign("right");
                        break;
                }

                //数据布局  左对齐、居中、 右对齐
                if(column.getDisplaywidth()>0)
                    info.setColumnWidth(column.getDisplaywidth());
                //排序方式 无，正序，倒序
                info.setOrdertype(column.getIs_order());
                //是否汇总
                if("1".equals(column.getIs_sum()))
                    info.setSummaryType(ColumnsInfo.SUMMARYTYPE_SUM);
                else
                    info.setSummaryType(0);
                if(fi != null) {
                    info.setCodesetId(fi.getCodesetid());
                    info.setColumnType(fi.getItemtype());
                    info.setDecimalWidth(fi.getDecimalwidth());
                    info.setColumnLength(fi.getItemlength());
                    info.setColumnDesc(fi.getItemdesc());
                    if("N".equals(fi.getItemtype())){
                        info.setFilterable(true);
                    }
                }
                if(column.getDisplaydesc()!=null && column.getDisplaydesc().length()>0) {
                    info.setColumnDesc(column.getDisplaydesc());
                    info.setColumnRealDesc(column.getItemdesc());
                }else {
                    if(fi==null){
                        if("B0110".equalsIgnoreCase(columnId)){
                            info.setColumnDesc("单位名称");
                            info.setCodesetId("UN");
                        }else if("E0122".equalsIgnoreCase(columnId)){
                            info.setColumnDesc("部门");
                            info.setCodesetId("UM");
                        }else if("E01A1".equalsIgnoreCase(columnId)){
                            info.setColumnDesc("岗位");
                            info.setCodesetId("@K");
                        }else if("a0101".equalsIgnoreCase(columnId)){
                            info.setColumnDesc("姓名");
                        }else if("needFill".equalsIgnoreCase(columnId)){
                            info.setColumnDesc("应提交天数");
                        }else if("submited".equalsIgnoreCase(columnId)){
                            info.setColumnDesc("已提交天数");
                        }else if("timelysubmit".equalsIgnoreCase(columnId)){
                            info.setColumnDesc("按时提交天数");
                        }else if("overduefill".equalsIgnoreCase(columnId)){
                            info.setColumnDesc("逾期补填天数");
                        }else if("submitPercent".equalsIgnoreCase(columnId)){
                            info.setColumnDesc("提交率");
                        }else if("timelysubrate".equalsIgnoreCase(columnId)){
                            info.setColumnDesc("及时提交率");
                        }else if("routine".equalsIgnoreCase(columnId)){
                            info.setColumnDesc("例行工作时长（分钟）");
                        }else if("routinePercent".equalsIgnoreCase(columnId)){
                            info.setColumnDesc("例行工作时长占比");
                        }else if("keypoint".equalsIgnoreCase(columnId)){
                            info.setColumnDesc("重点工作时长（分钟）");
                        }else if("keypointPercent".equalsIgnoreCase(columnId)){
                            info.setColumnDesc("重点工作时长占比");
                        }else if("other".equalsIgnoreCase(columnId)){
                            info.setColumnDesc("其它工作时长（分钟）");
                        }else if("otherPercent".equalsIgnoreCase(columnId)){
                            info.setColumnDesc("其它工作时长占比");
                        }else if("timesCount".equalsIgnoreCase(columnId)){
                            info.setColumnDesc("总时长（分钟）");
                        }
                    }
                }
                info.displayIndex=i;
                //锁列
                if("1".equals(column.getIs_lock()))
                    info.setLocked(true);
                else
                    info.setLocked(false);
                //如果不是二级表头
                if(column.getMergedesc()==null || column.getMergedesc().length()==0){
                    if(!mergeColumns.isEmpty()){
                        ColumnsInfo compositedColumn = new ColumnsInfo();
                        compositedColumn.setColumnDesc(mergeDesc);
                        compositedColumn.setLocked(mergeLockState);
                        compositedColumn.setChildColumns((ArrayList)mergeColumns.clone());
                        infolist.add(compositedColumn);

                        mergeColumns.clear();
                        mergeDesc = "";
                        mergeLockState = null;
                        mergeIndex = 0;
                    }
                    infolist.add(info);
                    continue;
                }

                // 如果 复合表头 list 为空， 或者   与上一个 columns 复合表头名称相同，则添加进复合表头list
                if(mergeColumns.size()==0 || (column.getMergedesc().equals(mergeDesc) &&  i == mergeIndex+1)){
                    mergeColumns.add(info);
                    mergeDesc = column.getMergedesc();
                    mergeIndex = i;
                    if(mergeLockState==null || mergeLockState.booleanValue())
                        mergeLockState = Boolean.valueOf(info.isLocked());
                    continue;
                }else if(!column.getMergedesc().equals(mergeDesc)){
                    //如果 复合表头名称跟上一次的不一样了，那就是一个新的复合表头。将以前的 复合表头list 里的column 组合成复合表头，存入 newColumns 中，并重新初始化复合表头的一些参数
                    ColumnsInfo compositedColumn = new ColumnsInfo();
                    compositedColumn.setColumnDesc(mergeDesc);
                    compositedColumn.setLocked(mergeLockState);
                    compositedColumn.setChildColumns((ArrayList)mergeColumns.clone());
                    infolist.add(compositedColumn);

                    mergeColumns.clear();

                    mergeColumns.add(info);
                    mergeDesc = column.getMergedesc();
                    mergeLockState = Boolean.valueOf(info.isLocked());
                    mergeIndex = i;
                }else{//剩下的情况就不需要处理了，都是不符合复合表头定义规则的，直接当做普通列
                    infolist.add(info);
                }
            }
            if(!mergeColumns.isEmpty()){
                ColumnsInfo compositedColumn = new ColumnsInfo();
                compositedColumn.setColumnDesc(mergeDesc);
                compositedColumn.setLocked(mergeLockState);
                compositedColumn.setChildColumns((ArrayList)mergeColumns.clone());
                infolist.add(compositedColumn);
            }
            ColumnsInfo info=new ColumnsInfo();
            info.setColumnDesc("人员id");
            info.setColumnType("A");
            info.setColumnId("a0100");
            info.setEditableValidFunc("false");
            info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
            info.setEncrypted(true);
            infolist.add(info);

            info=new ColumnsInfo();
            info.setColumnDesc("库标识");
            info.setColumnType("A");
            info.setColumnId("nbase");
            info.setEditableValidFunc("false");
            info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
            info.setEncrypted(true);
            infolist.add(info);

            info=new ColumnsInfo();
            info.setColumnDesc("日期");
            info.setColumnType("D");
            info.setColumnId("p0104");
            info.setEditableValidFunc("false");
            info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
            infolist.add(info);
            return infolist;
        }
		ColumnsInfo info=new ColumnsInfo();
		info.setColumnDesc("单位名称");
		info.setColumnType("A");
		info.setColumnId("B0110");
		info.setCodesetId("UN");
		info.setEditableValidFunc("false");
		info.setLocked(true);
		infolist.add(info);
		
		info=new ColumnsInfo();
		info.setColumnDesc("部门");
		info.setColumnType("A");
		info.setColumnId("E0122");
		info.setCodesetId("UM");
		info.setEditableValidFunc("false");
		info.setRendererFunc("employeLog_me.showUplevelCode");
		info.setLocked(true);
		infolist.add(info);
		
		info=new ColumnsInfo();
		info.setColumnDesc("岗位");
		info.setColumnType("A");
		info.setColumnId("E01A1");
		info.setCodesetId("@K");
		info.setEditableValidFunc("false");
		info.setLocked(true);
		infolist.add(info);
		
		info=new ColumnsInfo();
		info.setColumnDesc("姓名");
		info.setColumnType("A");
		info.setColumnId("a0101");
		info.setEditableValidFunc("false");
		info.setRendererFunc("employeLog_me.showWorkLog");
		info.setLocked(true);
		infolist.add(info);

		info=new ColumnsInfo();
		info.setColumnDesc("人员id");
		info.setColumnType("A");
		info.setColumnId("a0100");
		info.setEditableValidFunc("false");
		info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
		info.setEncrypted(true);
		infolist.add(info);
		
		info=new ColumnsInfo();
		info.setColumnDesc("库标识");
		info.setColumnType("A");
		info.setColumnId("nbase");
		info.setEditableValidFunc("false");
		info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
		info.setEncrypted(true);
		infolist.add(info);
		
		
		info=new ColumnsInfo();
		info.setColumnDesc("日期");
		info.setColumnType("D");
		info.setColumnId("p0104");
		info.setEditableValidFunc("false");
		info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
		infolist.add(info);
		
		//haosl 员工日志监控优化 20170120
		info=new ColumnsInfo();
		info.setColumnDesc("应提交天数");
		info.setColumnType("N");
		info.setTextAlign("right");
		info.setEditableValidFunc("false");
		info.setColumnId("needFill");
		infolist.add(info);
		
		info=new ColumnsInfo();
		info.setColumnDesc("已提交天数");
		info.setColumnType("N");
		info.setTextAlign("right");
		info.setEditableValidFunc("false");
		info.setColumnId("submited");
		infolist.add(info);
		
		
		info=new ColumnsInfo();
		info.setColumnDesc("按时提交天数");
		info.setColumnType("N");
		info.setTextAlign("right");
		info.setEditableValidFunc("false");
		info.setColumnId("timelysubmit");
		infolist.add(info);
		
		info=new ColumnsInfo();
		info.setColumnDesc("逾期补填天数");
		info.setColumnType("N");
		info.setTextAlign("right");
		info.setColumnId("overduefill");
		info.setEditableValidFunc("false");
		infolist.add(info);
		

		info=new ColumnsInfo();
		info.setColumnDesc("提交率");//提交率（已提交/应提交）
		info.setColumnType("A");
		info.setTextAlign("right");
		info.setEditableValidFunc("false");
		info.setColumnId("submitPercent");
		infolist.add(info);
		
		info=new ColumnsInfo();
		info.setColumnDesc("及时提交率");
		info.setColumnType("A");
		info.setTextAlign("right");
		info.setEditableValidFunc("false");
		info.setColumnId("timelysubrate");
		infolist.add(info);
		
		//haosl 员工日志监控优化 20170120
		info=new ColumnsInfo();
		info.setColumnDesc("例行工作时长（分钟）");
		info.setColumnType("N");
		info.setColumnWidth(150);
		info.setTextAlign("right");
		info.setEditableValidFunc("false");
		info.setColumnId("routine");
		infolist.add(info);
		
		info=new ColumnsInfo();
		info.setColumnDesc("例行工作时长占比");
		info.setColumnType("A");
		info.setColumnWidth(130);
		info.setTextAlign("right");
		info.setEditableValidFunc("false");
		info.setColumnId("routinePercent");
		infolist.add(info);
		
		info=new ColumnsInfo();
		info.setColumnDesc("重点工作时长（分钟）");
		info.setColumnWidth(150);
		info.setColumnType("N");
		info.setTextAlign("right");
		info.setEditableValidFunc("false");
		info.setColumnId("keypoint");
		infolist.add(info);
		
		info=new ColumnsInfo();
		info.setColumnDesc("重点工作时长占比");
		info.setColumnWidth(130);
		info.setColumnType("A");
		info.setTextAlign("right");
		info.setEditableValidFunc("false");
		info.setColumnId("keypointPercent");
		infolist.add(info);
		
		info=new ColumnsInfo();
		info.setColumnDesc("其它工作时长（分钟）");
		info.setColumnWidth(150);
		info.setColumnType("N");
		info.setTextAlign("right");
		info.setEditableValidFunc("false");
		info.setColumnId("other");
		infolist.add(info);
		
		info=new ColumnsInfo();
		info.setColumnDesc("其它工作时长占比");
		info.setColumnWidth(130);
		info.setColumnType("A");
		info.setTextAlign("right");
		info.setEditableValidFunc("false");
		info.setColumnId("otherPercent");
		infolist.add(info);
		
		info=new ColumnsInfo();
		info.setColumnDesc("总时长（分钟）");
		info.setColumnWidth(110);
		info.setColumnType("N");
		info.setTextAlign("right");
		info.setEditableValidFunc("false");
		info.setColumnId("timesCount");
		infolist.add(info);
		return infolist;
	}
	
	
	public String getSql(String currentYear) throws GeneralException{
		//取人员范围条件参数
        DbWizard dw = new DbWizard(this.conn);
		RecordVo paramsVo=ConstantParamter.getConstantVo("OKR_CONFIG");
		WorkPlanConfigBo bo = new WorkPlanConfigBo(this.conn, this.userview);
		String xmlValue = "";
		Map mapXml = new HashMap();
		// 有缓存则取缓存数据
		if(null != paramsVo){
			xmlValue = paramsVo.getString("str_value");
		}
		mapXml = bo.parseXml(xmlValue);
		String dbValue = mapXml.get("nbases")==null?"":(String)mapXml.get("nbases");
		String emp_scope = mapXml.get("emp_scope")==null?"":(String)mapXml.get("emp_scope");
		String[] nbaseArry=dbValue.split(",");
		if(nbaseArry.length<1){
			throw new GeneralException("未设置OKR人员库范围！");
		}
		StringBuffer nbaseSql=new StringBuffer();
		Map<String,String> map = getDateRange(currentYear);//选中月份的总天数
		String fromstr = map.get("from");
		String tostr = map.get("to");
		int workingDays = getDaysBayWorking(currentYear);	//获得当前月的工作日天数（除周六日）
		String b0110 = this.userview.getUnitIdByBusi("5");//OKR  取得所属单位
		//20170301 linbz 26080 增加权限范围 
		StringBuffer powSql=new StringBuffer();
		if(b0110.split("`")[0].length() > 2){//组织机构去除UN、UM后不为空：取本级，本级，下级。为空：最高权限
			String[] b0110Array = b0110.split("`");
			
			for(int i=0; i<b0110Array.length; i++){
				String b = b0110Array[i].substring(2);
				powSql.append(" e.b0110 like '"+b+"%' or e.e0122 like '"+b+"%' or e.e01a1 like '"+b+"%' ");//本级、下级
				if(i < b0110Array.length-1){
					powSql.append(" or ");
				}
			}
		}
		if(powSql.length()>0) {
			String temp = powSql.toString();
			powSql.setLength(0);
			powSql.append(" where (").append(temp).append(") "); 
		}
        String pyField = getPinYinFld();
		//20170301 linbz 26080  优化数据库查询效率
		for(int i=0;i<nbaseArry.length;i++){
			 String nab = nbaseArry[i];
			 String pyField_ = "";
            if(StringUtils.isNotBlank(pyField) && dw.isExistField(nab+"A01", pyField)) {
                pyField_= pyField +" as pinyin";
            }else {
                pyField_ = "'' as pinyin";
            }
			 nbaseSql.append(" select e.*,needFill as needFill_old,routine as routine_old,keypoint as keypoint_old,other as other_old,");
			 nbaseSql.append( "(case when e.a0100 is not null  then '"+nab+"' else  '"+nab+"' end) NBASE, ");
			 nbaseSql.append(pyField_);
			 nbaseSql.append(" from "+nab+"A01 e");
			 //应填天数
			 nbaseSql.append(" left join (SELECT kes.A0100,count(A0100) needFill FROM kq_employ_shift kes WHERE q03z0>='"+fromstr+"' AND q03z0<='"+tostr+"' and kes.class_id is not null and kes.class_id <> 0 and kes.nbase='"+nab+"' group by kes.A0100 )f  on f.A0100 = e.A0100 ");
			 //例行工作时长
			 nbaseSql.append(" left join (select p01.a0100,sum(work_time) routine from per_diary_content pdc,p01 WHERE p01.p0104>="+Sql_switcher.dateValue(fromstr)+" and p01.p0104<="+Sql_switcher.dateValue(tostr)+" and pdc.P0100=p01.P0100 and p01.flag in (0,1) and pdc.work_type='01' and p01.nbase='"+nab+"' group by p01.a0100)g on e.A0100=g.A0100 ");
			 //重点工作时长
			 nbaseSql.append(" left join (select  p01.a0100,sum(work_time) keypoint from per_diary_content pdc,p01 WHERE p01.p0104>="+Sql_switcher.dateValue(fromstr)+" and p01.p0104<="+Sql_switcher.dateValue(tostr)+" and pdc.P0100=p01.P0100 and p01.flag in (0,1) and pdc.work_type='02' and p01.nbase='"+nab+"' group by p01.a0100)h on e.A0100=h.A0100 ");
			 //其他工作时长
			 nbaseSql.append(" left join (select p01.a0100,sum(work_time) other from per_diary_content pdc,p01 WHERE p01.p0104>="+Sql_switcher.dateValue(fromstr)+" and p01.p0104<="+Sql_switcher.dateValue(tostr)+" and pdc.P0100=p01.P0100 and p01.flag in (0,1) and pdc.work_type='03' and p01.nbase='"+nab+"' group by p01.a0100)j on e.A0100=j.A0100 ");
			 //权限
			 nbaseSql.append(powSql.toString());
			 
			 //OKR人员范围sql条件
			 String whereIn = WorkPlanConfigBo.getOkrWhereINSql(nab, emp_scope);
			 if(!StringUtils.isEmpty(whereIn)){
				 //需判断是否加where，否则报错  haosl 2018-2-12 add 
				 if(StringUtils.isBlank(powSql.toString())) {
					 nbaseSql.append(" where ");
				 }else {
					 nbaseSql.append(" and ");
				 }
				 nbaseSql.append("e.a0100 in (");
				 nbaseSql.append("select "+nab+"A01.a0100 ").append(whereIn).append(") ");
			 }
			 if(i<nbaseArry.length-1)
				 nbaseSql.append(" union all ");
		}
		
		StringBuffer sbf=new StringBuffer();
		sbf.append("select c.*,"+Sql_switcher.toInt("d.submited")+" submited,"+Sql_switcher.toInt("d.timelysubmit")+" timelysubmit,"+Sql_switcher.toInt("d.overduefill")+" overduefill,");
		sbf.append("(case when c.needFill_old=0 then "+workingDays+" when c.needFill_old is null then "+workingDays+" else c.needFill_old  end) needFill,");//应填天数
		sbf.append(Sql_switcher.toInt("c.routine_old")+" routine,");//例行工作时长
		sbf.append(Sql_switcher.toInt("c.keypoint_old")+" keypoint,");//重点工作时长
		sbf.append(Sql_switcher.toInt("c.other_old")+" other,");//其他工作时长
		//总时长
		String  timesCountSql = Sql_switcher.isnull("c.routine_old","0")+"+"+Sql_switcher.isnull("c.keypoint_old","0")+"+"+Sql_switcher.isnull("c.other_old","0");
		sbf.append(timesCountSql+" timesCount,");
		//提交率
		sbf.append("nullif(cast(round("+Sql_switcher.charToFloat("d.submited")+"/case when c.needFill_old=0 then "+workingDays+" when c.needFill_old is null then "+workingDays+" else c.needFill_old  end*100,2) AS varchar(50))"+Sql_switcher.concat()+"'%','%') submitPercent,");
		//及时提交率
		sbf.append("nullif(cast(round("+Sql_switcher.charToFloat("d.timelysubmit")+"/case when c.needFill_old=0 then "+workingDays+" when c.needFill_old is null then "+workingDays+" else c.needFill_old  end*100,2) AS varchar(50))"+Sql_switcher.concat()+"'%','%') timelysubrate,");
		//例行工作时长占比
		sbf.append("nullif(cast(round("+Sql_switcher.charToFloat("c.routine_old")+"/"+Sql_switcher.charToFloat("nullif("+timesCountSql+",0)")+"*100,2) AS varchar(50))"+Sql_switcher.concat()+"'%','%') routinePercent,");
		//重点工作时长占比
		sbf.append("nullif(cast(round("+Sql_switcher.charToFloat("c.keypoint_old")+"/"+Sql_switcher.charToFloat("nullif("+timesCountSql+",0)")+"*100,2) AS varchar(50))"+Sql_switcher.concat()+"'%','%') keypointPercent,");
		//其他工作时长占比
		sbf.append("nullif(cast(round("+Sql_switcher.charToFloat("c.other_old")+"/"+Sql_switcher.charToFloat("nullif("+timesCountSql+",0)")+"*100,2) AS varchar(50))"+Sql_switcher.concat()+"'%','%') otherPercent");
		sbf.append( " from ("+nbaseSql.toString()+")c" );
		sbf.append(" left join ( ");
		sbf.append(" select b.* from( ");
		sbf.append(" select a.A0100,(a.overduefill+a.timelysubmit)submited,a.timelysubmit,a.overduefill,a.p0104,a.nbase");
		sbf.append(" from ( ");
		
		sbf.append(" select A0100,COUNT(case  when flag=0 then 'timelysubmit' end)timelysubmit, ");
		sbf.append(" COUNT(case when flag=1 then 'overduefill' end)overduefill,p0104,nbase ");
		sbf.append(" from (select a0100,flag,"+Sql_switcher.dateToChar("P0104","yyyy-MM")+" p0104,state,nbase from P01) P01 where state=0 and  flag in (0,1) and p0104='"+currentYear+"' group by A0100,p0104,nbase ");
		sbf.append(" )a )b ");
		sbf.append(" )d on c.A0100=d.A0100 ");
		
		return sbf.toString();
	}

    /**
     * 取得拼音指标
     *
     * @Title: getPinYinFld
     * @Description:
     * @return
     */
    private String getPinYinFld() {
        // 获取拼音简码的字段
        Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.conn);
        String pinyinFld = sysbo.getValue(Sys_Oth_Parameter.PINYIN_FIELD_SEARCH);
        if (null == pinyinFld || "".equals(pinyinFld.trim()))
            pinyinFld = "";

        if (!fieldInA01(pinyinFld))
            pinyinFld = "";

        return pinyinFld;
    }
    /**
     * 是否是主集指标并已构库
     *
     * @Title: fieldInA01
     * @Description:
     * @param field
     * @return
     */
    private boolean fieldInA01(String field) {
        boolean inA01 = false;
        if (null == field || "".equals(field.trim()))
            return inA01;

        FieldItem fieldItem = DataDictionary.getFieldItem(field, "a01");
        inA01 = null != fieldItem && "1".equals(fieldItem.getUseflag());

        return inA01;
    }
	/** 应用库 认证人员库 */
	public static String[] getNbase() {
		RecordVo login_vo = ConstantParamter.getConstantVo("SS_LOGIN");
		if (login_vo != null) {
			String strpres = login_vo.getString("str_value");
			return strpres.split(",");
		}
		
		return new String[0];
	}
	/**
	 * 获得当前月份的天数
	 * @param currentTime
	 * @return
	 * @throws GeneralException 
	 */
	private Map<String,String> getDateRange(String currentTime) throws GeneralException{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
		Map<String,String> map = new HashMap<String,String>();
		if(StringUtils.isBlank(currentTime))
			return map;
		int days = 0;
		try {
			Date date = format.parse(currentTime);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			days = calendar.getActualMaximum(Calendar.DATE);
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			format = new SimpleDateFormat("yyyy.MM.dd");
			String fromDate = format.format(calendar.getTime());
			calendar.set(Calendar.DAY_OF_MONTH, days);
			String toDate = format.format(calendar.getTime());
			map.put("from", fromDate);
			map.put("to", toDate);
		} catch (ParseException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return map;
	}
	/**
	 * 获得制定月份的工作日（除周六日）
	 * @param currentTime(yyyy-MM)
	 * @author haosl
	 * @return
	 */
	private int getDaysBayWorking(String currentTime){
		if(StringUtils.isBlank(currentTime))
			return 0;
		String[] yearMonth = currentTime.split("-");
		if(yearMonth.length!=2)
			return 0;
		Calendar calendar = Calendar.getInstance();
		calendar.set(Integer.parseInt(yearMonth[0]), Integer.parseInt(yearMonth[1])-1, 1);//设置月份的第一天
		int days = calendar.getActualMaximum(Calendar.DATE);
		int result = days;
		for(int i=0;i<days;i++){
			calendar.add(Calendar.DATE,i==0?0:1);
			if(cheackHolidays(calendar)){
				result--;
			}
		}
		return result;
	}
	/**
	 * 校验是否为休息日
	 */
	private boolean cheackHolidays(Calendar cal){
		if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY||cal.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY)
			return true;
		return false;
	}
}
