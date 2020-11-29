package com.hjsj.hrms.module.kq.config.period.businessobject.impl;

import com.hjsj.hrms.module.kq.config.period.businessobject.PeriodService;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.ButtonInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.*;

public class PeriodServiceImpl implements PeriodService {
	
	private UserView userView;
	private Connection conn;
	
	public PeriodServiceImpl() {
		
	}
	
	public PeriodServiceImpl(UserView userView,Connection conn) {
		this.userView = userView;
		this.conn = conn;
	}
	
	@Override
    public ArrayList<LazyDynaBean> listKq_duration(String sqlWhere,
                                                   ArrayList parameterList, String sqlSort) throws GeneralException {
	   ContentDAO dao = new ContentDAO(this.conn);
        ArrayList<LazyDynaBean> dataList = new ArrayList<LazyDynaBean>();
        RowSet rs = null;
        StringBuffer strSql = new StringBuffer();
        try {
            strSql.append("SELECT kq_year,kq_duration,kq_start,kq_end,");
            strSql.append(Sql_switcher.isnull("gz_duration", "''")).append(" gz_duration,");
            strSql.append(Sql_switcher.isnull("gz_year", "''")).append(" gz_year");
            strSql.append(" FROM kq_duration where 1=1 ");
            if (sqlWhere != null) {
                strSql.append(sqlWhere);
            }
            if (sqlSort != null && sqlSort.length() > 0) {
                strSql.append(" ORDER BY ").append(sqlSort);
            } else {
                strSql.append(" ORDER BY kq_year,kq_duration ");
            }
            ArrayList pList = new ArrayList();
            if (parameterList != null) {
                pList.addAll(parameterList);
            }
            rs = dao.search(strSql.toString(), pList);

            while (rs.next()) {
                //查询结果 bean 中的key为全小写字段名
                LazyDynaBean bean = new LazyDynaBean();
                bean.set("kq_year", rs.getString("kq_year"));
                bean.set("kq_duration", rs.getString("kq_duration"));
                bean.set("kq_start", rs.getDate("kq_start"));
                bean.set("kq_end", rs.getDate("kq_end"));
                bean.set("gz_duration", rs.getString("gz_duration"));
                bean.set("gz_year", rs.getString("gz_year"));
                dataList.add(bean);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(new Exception("查询数据出错！"));
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return dataList;
	}
	
	@Override
    public String getTableSql() {
		StringBuffer sql = new StringBuffer();
		sql.append("select kq_year,kq_duration,kq_start,kq_end from kq_duration");
		return sql.toString();
	}
	private ArrayList getColumnList() {
		ArrayList<ColumnsInfo> list = new ArrayList<ColumnsInfo>();
		ColumnsInfo kq_duration = getColumnInfo(ResourceFactory.getProperty("kq.deration_details.kqqj"), 120,"kq_duration");
		list.add(kq_duration);
		ColumnsInfo kq_start = getColumnInfo(ResourceFactory.getProperty("kq.deration_details.start"), 200,"kq_start");
		kq_start.setColumnType("D");
		kq_start.setColumnLength(10);
		list.add(kq_start);
		ColumnsInfo kq_end = getColumnInfo(ResourceFactory.getProperty("kq.deration_details.end"), 200,"kq_end");
		kq_end.setColumnType("D");
		kq_end.setColumnLength(10);
		list.add(kq_end);
		ColumnsInfo kq_year = getColumnInfo(ResourceFactory.getProperty("kq.deration_details.kqnd"), 100,"kq_year");
		kq_year.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
		list.add(kq_year);
		
		return list;
	}
	private ArrayList getButtonList() {
		ArrayList buttonList = new ArrayList();
		ArrayList newBtnList = new ArrayList();
		if(this.userView.hasTheFunction("272050201")) {
				//同上一年度
			if(this.userView.hasTheFunction("27205020101"))
				newBtnList.add(getMenuBean(ResourceFactory.getProperty("kq.duration.samen"), "config_period_me.addPeriod('1')", "", null));
			//按自然月
			if(this.userView.hasTheFunction("27205020102"))
				newBtnList.add(getMenuBean(ResourceFactory.getProperty("kq.duration.amonth"), "config_period_me.addPeriod('2')", "", null));
			//指定期间
			if(this.userView.hasTheFunction("27205020103"))
				newBtnList.add(getMenuBean(ResourceFactory.getProperty("kq.duration.zdjj"), "config_period_me.addPeriod('3')", "", null));
			
			if(newBtnList.size()>0) {
				String menu = getMenuStr(ResourceFactory.getProperty("kq.search_feast.new"),"newbtn",newBtnList);
				buttonList.add(menu);
			}
		}
		if(this.userView.hasTheFunction("272050202")) {
			ButtonInfo delBtn = new ButtonInfo(ResourceFactory.getProperty("kq.search_feast.delete"), "config_period_me.deletePeriod");
			buttonList.add(delBtn);
		}
		return buttonList;
	}
	
	private ColumnsInfo getColumnInfo(String desc,int colWidth,String columnId) {
		ColumnsInfo column = new ColumnsInfo();
		column.setColumnDesc(desc);
		column.setColumnId(columnId);
		column.setTextAlign("center");
		column.setSortable(false);
		column.setOrdertype("0");
		column.setColumnWidth(colWidth);
		return column;
	}
	
	/**
	 * 生成菜单的bean
	 * @param text    文本内容
	 * @param handler 触发事件
	 * @param icon    图标
	 * @param list    按钮集合
	 * @return
	 */
	private LazyDynaBean getMenuBean(String text,String handler,String icon,ArrayList list){
		LazyDynaBean bean = new LazyDynaBean();
		try{
			if(text!=null&&text.length()>0)
				bean.set("text", text);
			if(icon!=null&&icon.length()>0)
				bean.set("icon", icon);
			if(handler!=null&&handler.length()>0){
				if(list!=null&&list.size()>0){
					bean.set("menu", list);
				}else{
					bean.set("handler", handler);
				}				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bean;
	}
	/**
	 * 生成新建按钮的json串
	 * @param name 菜单名
	 * @param id   菜单id
	 * @param list 菜单功能集合
	 * @return
	 */
	private String getMenuStr(String name,String id,ArrayList list){
		StringBuffer str = new StringBuffer();
		try{
			if(name.length()>0){
				str.append("<jsfn>{xtype:'button',text:'"+name+"'");
			}
			if(StringUtils.isNotBlank(id)){
				str.append(",id:'");
				str.append(id);
				str.append("'");
			}
			str.append(",menu:{items:[");
			for(int i=0;i<list.size();i++){
				LazyDynaBean bean = (LazyDynaBean) list.get(i);
				if(i!=0)
					str.append(",");
				str.append("{");
				if(bean.get("xtype")!=null&&bean.get("xtype").toString().length()>0)
					str.append("xtype:'"+bean.get("xtype")+"'");
				if(bean.get("text")!=null&&bean.get("text").toString().length()>0)
					str.append("text:'"+bean.get("text")+"'");
				if(bean.get("handler")!=null&&bean.get("handler").toString().length()>0){
					if(bean.get("xtype")!=null&& "datepicker".equalsIgnoreCase(bean.get("xtype").toString())){//时间控件单独处理一下 方法GzGlobal.aaa(picker, date)这样写
						str.append(",handler:function(picker, date){"+bean.get("handler")+";}");
					}else{
						str.append(",handler:function(){"+bean.get("handler")+";}");
					}				
				}
				String menuId = (String)bean.get("id");
				
				if(menuId!=null&&menuId.length()>0)//人事异动-手工选择按钮需要id（gaohy）
					str.append(",id:'"+menuId+"'");
				else
					menuId = "";
				if(bean.get("icon")!=null&&bean.get("icon").toString().length()>0)
					str.append(",icon:'"+bean.get("icon")+"'");
				if(bean.get("value")!=null&&bean.get("value").toString().length()>0)
					str.append(",value:"+bean.get("value")+"");
				ArrayList menulist = (ArrayList)bean.get("menu");
				if(menulist!=null&&menulist.size()>0){
					str.append(getMenuStr("",menuId, menulist));
				}
				str.append("}");
			}
			str.append("]}");
			if(name.length()>0){				
				str.append("}</jsfn>");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str.toString();
	}
	
	@Override
    public String deleteDurations(String kq_year, List<String> kq_durations) {
		ContentDAO dao = new ContentDAO(this.conn);
		JSONObject obj = new JSONObject();
		String return_code = "success";
		String return_msg = ResourceFactory.getProperty("kq.search_feast.delete")+ResourceFactory.getProperty("kq.duration.mess.success");
		RowSet rs = null;
		try {
			//判断是否可以删除期间
			String sql = "select distinct(kq_duration) from kq_extend_log where kq_year=? and kq_duration in (";
			String pholder = "";
			for(int i=0;i<kq_durations.size();i++) {
				pholder+="?";
				if(i<kq_durations.size()-1) {
					pholder+=",";
				}
			}
			sql+=pholder+")";
			List<String> values = new ArrayList<String>();
			values.add(kq_year);
			values.addAll(kq_durations);
			rs = dao.search(sql, values);
			int i=0;
			String msg = "";
			while(rs.next()) {
				i++;
				if(i<=5)
					msg+=rs.getString("kq_duration")+"、";
				else 
					break;
				
			}
			if(i>0) {
				return_code = "fail";
				return_msg = ResourceFactory.getProperty("kq.deration_details.kqqj")+"【"+msg.substring(0,msg.length()-1)+"】"+(i>5?ResourceFactory.getProperty("kq.duration.andmore"):"")+ResourceFactory.getProperty("kq.duration.mess.nodel");
			}else {
				//执行删除
				sql = "delete from kq_duration where kq_year=? and kq_duration in (";
				sql+=pholder+")";
				dao.delete(sql, values);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return_code = "fail";
			return_msg = ResourceFactory.getProperty("kq.search_feast.delete")+ResourceFactory.getProperty("kq.duration.mess.fail");
		}finally {
			obj.put("return_code", return_code);
			obj.put("return_msg", return_msg);
		}
		return obj.toString();
	}
	
	@Override
    public boolean checkHasPrivPeriod(String kq_year) throws Exception {
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		boolean flag =  false;
		try {
			String sql = "select kq_year from kq_duration where kq_year=?";
			List<String> values = new ArrayList<String>();
			values.add(kq_year);
			rs = dao.search(sql, values);
			if(rs.next()) {
				flag = true;
			}
		}finally {
			PubFunc.closeDbObj(rs);
		}
		return flag;
	}
	@Override
    public String crteatePeriod(JSONObject jsonObj) {
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		JSONObject obj = new JSONObject();
		String return_code = "success";
		String return_msg = "";
		try {
			String kq_year = (String)jsonObj.get("kq_year");
			String model = (String)jsonObj.get("model");
			//同上年度
			if("1".equals(model)) {
				String privous_kq_year = String.valueOf(Integer.parseInt(kq_year)-1);
				StringBuffer sql = new StringBuffer();
				sql.append("select kq_year,kq_duration,kq_start,kq_end from kq_duration where kq_year=?");
				sql.append(" and kq_duration not in (select kq_duration from kq_duration where kq_year=?)");
				List values = new ArrayList();
				values.add(privous_kq_year);
				values.add(kq_year);
				rs = dao.search(sql.toString(), values);
				sql.setLength(0);
				sql.append("insert into kq_duration(kq_year,kq_duration,kq_start,kq_end,gz_year,gz_duration,finished) values (?,?,?,?,?,?,?)");
				List list = new ArrayList();
				Calendar cal = Calendar.getInstance();
				while(rs.next()) {
					List valueList = new ArrayList();
					valueList.add(kq_year);
					valueList.add(rs.getString("kq_duration"));
					Date startD = rs.getDate("kq_start");
					cal.setTime(startD);
					cal.add(Calendar.YEAR, 1);
					valueList.add(DateUtils.getSqlDate(cal.getTime()));
					Date endD = rs.getDate("kq_end");
					cal.setTime(endD);
					cal.add(Calendar.YEAR, 1);
					valueList.add(DateUtils.getSqlDate(cal.getTime()));
					valueList.add(kq_year);
					valueList.add(rs.getString("kq_duration"));
					valueList.add(0);//未封存
					list.add(valueList);
				}
				dao.batchInsert(sql.toString(), list);
			}
			//按自然月
			else if("2".equals(model)) {
				StringBuffer sql = new StringBuffer();
				sql.append("select kq_duration from kq_duration where kq_year=?");
				List values = new ArrayList();
				values.add(kq_year);
				rs = dao.search(sql.toString(),values);
				String kq_durations = ",";
				while(rs.next()) {
					kq_durations += rs.getString("kq_duration")+",";
				}
				
				sql.setLength(0);
				sql.append("insert into kq_duration(kq_year,kq_duration,kq_start,kq_end,gz_year,gz_duration,finished) values (?,?,?,?,?,?,?)");
				Calendar cale = Calendar.getInstance();
				cale.set(Calendar.YEAR,Integer.parseInt(kq_year));
				List list = new ArrayList();
				for(int i=0;i<12;i++) {
					String kq_month="";
					if(i<9) {
						kq_month = "0"+(i+1);
					}else {
						kq_month=(i+1)+"";
					}
					if(kq_durations.indexOf(","+kq_month+",")>-1) {
						continue;
					}
					List param = new ArrayList();
		    		cale.set(Calendar.MONTH, i);
			        cale.set(Calendar.DAY_OF_MONTH, 1);
			        Date firstday = cale.getTime();
			        cale.set(Calendar.DAY_OF_MONTH, cale.getActualMaximum(Calendar.DAY_OF_MONTH));
			        Date lastday = cale.getTime();
			        param.add(kq_year);
			        param.add(kq_month);
			        param.add(DateUtils.getSqlDate(firstday));
			        param.add(DateUtils.getSqlDate(lastday));
			        param.add(kq_year);
			        param.add(kq_month);
			        param.add(0);//未封存
			        list.add(param);
				}
				dao.batchInsert(sql.toString(), list);
			}else if("3".equals(model)) {
				int start_month = jsonObj.getInt("start_month");
				int start_day = jsonObj.getInt("start_day");
				String privios_month = jsonObj.getString("privios_month");////起始日期自上月起
				
				
				StringBuffer sql = new StringBuffer();
				sql.append("select kq_duration from kq_duration where kq_year=?");
				List values = new ArrayList();
				values.add(kq_year);
				rs = dao.search(sql.toString(),values);
				if(rs.next()) {
				    // 考勤期间已存在，不能重复创建，这里暂时不做处理，前端在创建时应先提示先删除原有数据
				    return obj.toString();
				}
				
				sql.setLength(0);
				sql.append("insert into kq_duration(kq_year,kq_duration,kq_start,kq_end,gz_year,gz_duration,finished) values (?,?,?,?,?,?,?)");
				
		        int s_month = start_month;
		        int lOffSet = s_month-1;
		        if("1".equals(privios_month))
		            lOffSet +=-1;
		        
		        // 考勤年度
		        int s_kyear = Integer.parseInt(kq_year);
		        int year = s_kyear;
		        
		        // 开始日期
		        int day = start_day;
		        if(day>31)
		            day=31;
		        
		        // 开始月份
		        if(s_month>12)
		            s_month=12;
		        
		        // 日期从上月起，如果月份是1月，那么上月在上1年
		        if("1".equals(privios_month) && s_month==1)
		            year = year - 1;
		            
		        int month=0;
		        Date sDate=null;
		        Date eDate=null;
		            
		        List durationList = new ArrayList();    
		        
	            for(int i=1;i<=12;i++) {
	                List param = new ArrayList();
	            
	                month= i + lOffSet;
	                if(month>12) {
	                    month = month-12;
	                    year=s_kyear+1;
	                } else if (month == 0) {
                        month = 12;
                    } 
	                
	                if (month == 2) {
                        int februaryDays = DateUtils.getDay((DateUtils.getDate(getDateByAfteri(year+"-"+month),"yyyy-MM-dd")));
                        // 2月份比较特殊，一般28天，闰年29天，如果开始日期在大于2月最后一天，那么开始日期只能取3月1号，否则取2月的日期
                        if (februaryDays < day - 1) {
                            sDate=DateUtils.getDate(year,month+1,1);
                        } else {
                            sDate=DateUtils.getDate(year,month,day);
                        }
                    } else {
                        sDate=DateUtils.getDate(year,month,day);
                    }
	                
	                    
	                if(day==1)
	                  month = month;
	                else
	                  month = month + 1;
	                
	                if(month<12) {
	                    if(day==1)
	                        eDate=DateUtils.getDate(getDateByAfteri(year+"-"+month),"yyyy-MM-dd");
	                    else {
	                        if (month == 2) {
	                            int februaryDays = DateUtils.getDay((DateUtils.getDate(getDateByAfteri(year+"-"+month),"yyyy-MM-dd")));
	                            // 2月份比较特殊，一般28天，闰年29天，如果结束日期大于2月最后一天，那么结束日期只能取2月最后一天，否则取2月的日期
	                            if (februaryDays < day - 1) {
	                                eDate=DateUtils.getDate(year,month,februaryDays);
                                } else {
                                    eDate=DateUtils.getDate(year,month,day-1);
                                }
                            } else {
                                eDate=DateUtils.getDate(year,month,day-1);
                            }
	                    }
	                } else {
	                    if(day==1) {
	                        if(month==12)
	                            eDate=DateUtils.getDate(getDateByAfteri(year+"-"+month),"yyyy-MM-dd");
	                        else
	                            eDate=DateUtils.getDate(getDateByAfteri((year+1)+"-"+(month-12)),"yyyy-MM-dd");
	                    } else {
	                        if(month==12)
	                            eDate=DateUtils.getDate(year,month,day-1);
	                        else
	                            eDate=DateUtils.getDate((year+1),(month-12),day-1);
	                    }
	                }   
	                
			        param.add(kq_year);
			        param.add(redate(i));
			        param.add(DateUtils.getSqlDate(sDate));
			        param.add(DateUtils.getSqlDate(eDate));
			        param.add(kq_year);
			        if(month>12)
			            param.add(redate(month-12));
			        else    
			            param.add(redate(month));
			        
			        param.add(0);//未封存
			        
			        durationList.add(param);
			        
			        if (month > 12) {
                        year = year + 1;
                    }
				}
	            dao.batchInsert(sql.toString(), durationList);
		    }
		}catch (Exception e) {
			e.printStackTrace();
			return_code = "fail";
			return_msg = ResourceFactory.getProperty("kq.deration_details.kqqj")+ResourceFactory.getProperty("kq.duration.mess.create")+ResourceFactory.getProperty("kq.duration.mess.fail");
		} finally {
			obj.put("return_code", return_code);
			obj.put("return_msg", return_msg);
			PubFunc.closeDbObj(rs);
		}
		return obj.toString();
		
	}
		
	private String redate(int str)
    {
        String ret="";
        if(String.valueOf(str).length()==1)
            ret="0"+str;
        else
            ret=String.valueOf(str);
        
        return ret;
    }
	
	/**
     * 取某年某月的最后一天
     * @param str 
     *         某年某月
     * @return string
     *          返回某年某月的最后一天
     * */
     private  String getDateByAfteri(String str) throws GeneralException
     {
         
         Calendar now = Calendar.getInstance();
         int maxDay =0;
         
         try {
                 Date date = new SimpleDateFormat("yyyy-MM").parse(str);
                 now.setTime(date);
                 maxDay = now.getActualMaximum(Calendar.DATE);
                 now.add(GregorianCalendar.DAY_OF_MONTH,maxDay-1);
                 
          }catch (Exception e) {
             e.printStackTrace();
             throw GeneralExceptionHandler.Handle(e);
         }
          return new SimpleDateFormat("yyyy-MM-dd").format(now.getTime());
     }
	
	@Override
    public List<LazyDynaBean> getYearList() throws GeneralException {
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		List<LazyDynaBean> yearList = new ArrayList<LazyDynaBean>();
		try {
			int curYear = DateUtils.getYear(new Date());
			String sql = "select distinct(kq_year) from kq_duration where kq_year<? order by kq_year asc";
			List<String> values = new ArrayList<String>();
			values.add(String.valueOf(curYear));
			rs = dao.search(sql, values);
			int firstYear = curYear;
			if(rs.next()) {
				firstYear = Integer.parseInt(rs.getString("kq_year"));
			}
			//循环生成年份
			for(int i=curYear+2;i>=firstYear;i--) {
				LazyDynaBean bean  = new LazyDynaBean();
				bean.set("name", i+ResourceFactory.getProperty("kq.wizard.year"));
				bean.set("value", String.valueOf(i));
				yearList.add(bean);
			}
		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		}finally {
			PubFunc.closeDbObj(rs);
		}
		return yearList;
		
	}

	@Override
    public String getShiftsTableConfig() {
		String datasql = this.getTableSql();
		ArrayList columnList = this.getColumnList();
		TableConfigBuilder builder = new TableConfigBuilder("period_list_subModuleId", columnList, "period", this.userView, this.conn);
		//默认显示当前年
		String kqYear = String.valueOf(DateUtils.getYear(new Date()));
		builder.setDataSql(datasql+" where kq_year='"+kqYear+"'");//数据查询sql语句
		builder.setOrderBy("order by kq_duration asc");//排序语句
		builder.setAutoRender(true);//是否自动渲染表格到页面
		builder.setTitle("考勤期间");// 标题
		builder.setSelectable(true);//选框
		builder.setEditable(false);//表格编辑
		ArrayList buttonList = this.getButtonList();//得到操作按钮
		builder.setTableTools(buttonList);//表格工具栏功能
		String config = builder.createExtTableConfig();
		return config;
	}
}
