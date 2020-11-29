package com.hjsj.hrms.module.kq.config.calendar.transaction;

import com.hjsj.hrms.businessobject.kq.register.IfRestDate;
import com.hjsj.hrms.module.kq.config.calendar.businessobject.impl.KqCalenderServiceImpl;
import com.hjsj.hrms.utils.OperateDate;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;
import com.hrms.virtualfilesystem.service.VfsService;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KqImportCalendarTrans extends IBusiness {

	HashMap<Integer, ArrayList<String>> msgMap = new HashMap<Integer, ArrayList<String>>();
	String holidayStr="";
	String turnRestStr="";
	@Override
	public void execute() throws GeneralException {
		String jsonStr = (String) this.formHM.get("jsonStr");
		// 获取前台json数据
		JSONObject jsonObj = JSONObject.fromObject(jsonStr);
		JSONObject returnJson = new JSONObject();
		String return_code = "success";
		String return_msg = "success";
		BufferedReader bufferedReader = null;
		InputStream stream = null;
		InputStreamReader reader =null;
		ArrayList<HashMap<String, String>> holidayList = new ArrayList<HashMap<String, String>>();
		ArrayList<HashMap<String, String>> turnRestList = new ArrayList<HashMap<String, String>>();
		try {
			String fileid = jsonObj.getString("fileid");
			stream=VfsService.getFile(fileid);
			reader = new InputStreamReader(stream);
			bufferedReader = new BufferedReader(reader);
			String line = bufferedReader.readLine();
			int i = 0;
			while (line != null) {
				i++;
				Pattern p = Pattern.compile("\\s+");
				Matcher m = p.matcher(line);
				line= m.replaceAll(" ");
				String[] data =StringUtils.split(line," ");
				// 内容格式不对公休日倒休
				if (data.length < 2 || data.length > 3) {
					ArrayList<String> sb = this.msgMap.get(i);
					if (sb == null)
						sb = new ArrayList<String>();
					String msg = "&nbsp;第" +i + "行" + ResourceFactory.getProperty("kq.search_feast.error.space");
					sb.add(msg);
					this.msgMap.put(i, sb);
					line = bufferedReader.readLine();
					continue;
				}
				if (data[0].indexOf("公休日倒休")!=-1) {
				    if (data.length < 3) {
				        ArrayList<String> sb = this.msgMap.get(i);
                        if (sb == null)
                            sb = new ArrayList<String>();
                        String msg = "&nbsp;第" +i + "行" + ResourceFactory.getProperty("kq.rest.spaceError");
                        sb.add(msg);
                        this.msgMap.put(i, sb);
                        line = bufferedReader.readLine();
                        continue;
				    }
					HashMap<String, String> turnRestMap = new HashMap<String, String>();
					turnRestMap.put("num", i + "");
					turnRestMap.put("weekDay", data[1]);
					turnRestMap.put("turnDay", data[2]);
					turnRestList.add(turnRestMap);
				} else {
					if (data.length > 2) {
						ArrayList<String> sb = this.msgMap.get(i);
						if (sb == null)
							sb = new ArrayList<String>();
						String msg = "&nbsp;第" +i + "行" + ResourceFactory.getProperty("kq.search_feast.error.space");
						sb.add(msg);
						this.msgMap.put(i, sb);
						line = bufferedReader.readLine();
						continue;
					}
					HashMap<String, String> holidayMap = new HashMap<String, String>();
					holidayMap.put("num", i + "");
					holidayMap.put("feastName", data[0]);
					holidayMap.put("feastDate", data[1]);
					holidayList.add(holidayMap);
				}
				line = bufferedReader.readLine();
			}
			// 校验公休日倒休
			for (HashMap<String, String> turnRest : turnRestList) {
				checkTurnDate(turnRest.get("num"),turnRest.get("weekDay"), turnRest.get("turnDay"));
			}
			// 校验节假日
			for (HashMap<String, String> holiday : holidayList) {
				String year=checkHoliday(holiday.get("num"),holiday.get("feastName"), holiday.get("feastDate"));
				holiday.put("kqYear", year);
			}
			if (getErrorMsg().length()==0) {
				KqCalenderServiceImpl kqCalenderService=new KqCalenderServiceImpl(this.getUserView(), this.getFrameconn());
				//校验通过保存数据
				kqCalenderService.saveHolidayForList(holidayList);
				kqCalenderService.saveTurnRestForList(turnRestList);
				
			}
			JSONObject obj = new JSONObject();
			obj.put("list", getErrorMsg());
			returnJson.put("return_data", obj);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(bufferedReader);
			PubFunc.closeDbObj(reader);
			PubFunc.closeDbObj(stream);
			returnJson.put("return_code", return_code);
			returnJson.put("return_msg", return_msg);
		}
		this.formHM.put("returnStr", returnJson.toString());
	}
	//检验公休日
	private void checkTurnDate(String num,String rdate, String tdate) {
		if (tdate.length()!=10||rdate.length()!=10||!isValidDate(rdate)||!isValidDate(tdate)) {
			ArrayList<String> sb = this.msgMap.get(Integer.valueOf(num));
			if (sb == null)
				sb = new ArrayList<String>();
			String msg = "&nbsp;第" +num + "行" + ResourceFactory.getProperty("kq.rest.rmach");
			sb.add((sb.size() + 1) + ".&nbsp;" + msg);
			this.msgMap.put(Integer.valueOf(num), sb);
			return;
		}
		//判断数据重复
		if (turnRestStr.indexOf(rdate)!=-1||turnRestStr.indexOf(tdate)!=-1) {
			ArrayList<String> sb = this.msgMap.get(Integer.valueOf(num));
			if (sb == null)
				sb = new ArrayList<String>();
			String msg = "&nbsp;第" +num + "行" + ResourceFactory.getProperty("kq.search_feast.error.same");
			sb.add((sb.size() + 1) + ".&nbsp;" + msg);
			this.msgMap.put(Integer.valueOf(num), sb);
			return;
		}
		turnRestStr +=","+rdate+","+tdate;
		ArrayList restList = IfRestDate.search_RestOfWeek("UN", userView, this.getFrameconn());
		String rest_date = restList.get(0).toString();

		String date = is_RestDate2(rdate.replaceAll("-", "\\."), userView, rest_date);
		String rest_state = ResourceFactory.getProperty("kq.date.work");

		String dates = IfRestDate.is_RestDate2(tdate.replaceAll("-", "\\."), userView, rest_date, "UN",
				this.getFrameconn());
		String rest = ResourceFactory.getProperty("kq.date.rest");
		if (dates.indexOf(rest) != -1) {// 5
			ArrayList<String> sbList = this.msgMap.get(Integer.valueOf(num));
			if (sbList == null)
				sbList = new ArrayList<String>();
			String msg = "&nbsp;第" +num + "行" + ResourceFactory.getProperty("kq.search_feast.error.five");
			sbList.add((sbList.size() + 1) + ".&nbsp;" + msg);
			this.msgMap.put(Integer.valueOf(num), sbList);
			return;
		}

		if (date.indexOf(rest_state) != -1) {// 2
			ArrayList<String> sbList = this.msgMap.get(Integer.valueOf(num));
			if (sbList == null)
				sbList = new ArrayList<String>();
			String msg = "&nbsp;第" +num + "行" + ResourceFactory.getProperty("kq.search_feast.error.two");
			sbList.add((sbList.size() + 1) + ".&nbsp;" + msg);
			this.msgMap.put(Integer.valueOf(num), sbList);
			return;
		} else {
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			RecordVo vo = new RecordVo("kq_turn_rest");
			StringBuffer sb = new StringBuffer();
			String work = "";
			sb.append("select week_date from kq_turn_rest where week_date=");
			sb.append(Sql_switcher.dateValue(rdate));
			sb.append(" and b0110='UN'");
			String turn = "";
			try {
				this.frowset = dao.search(sb.toString());
				if (this.frowset.next()) {
					Object obj = this.frowset.getObject("week_date");
					if (obj instanceof Date) {
						work = OperateDate.dateToStr((Date) obj, "yyyy.MM.dd");
					} else if (obj instanceof String) {
						work = (String) obj;
					}
				}

				sb.delete(0, sb.length());
				sb.append("select turn_date from kq_turn_rest where turn_date=");
				sb.append(Sql_switcher.dateValue(tdate));
				sb.append(" and b0110='UN'");
				this.frowset = dao.search(sb.toString());
				if (this.frowset.next()) {
					Object obj = this.frowset.getObject("turn_date");
					if (obj instanceof Date) {
						turn = OperateDate.dateToStr((Date) obj, "yyyy.MM.dd");
					} else if (obj instanceof String) {
						turn = (String) obj;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			if ((work == null || "".equals(work)) && (turn == null || "".equals(turn))) {
				
			} else {// 34
				ArrayList<String> sbList = this.msgMap.get(Integer.valueOf(num));
				if (sbList == null)
					sbList = new ArrayList<String>();
				String msg = "&nbsp;第" +num + "行" + ResourceFactory.getProperty("kq.search_feast.error.three");
				sbList.add((sbList.size() + 1) + ".&nbsp;" + msg);
				this.msgMap.put(Integer.valueOf(num), sbList);
				return;
			}
		}
	}
	// 校验节假日
	private String checkHoliday(String num,String feastName, String feastDate) {
		KqCalenderServiceImpl kqCalenderService=new KqCalenderServiceImpl(this.getUserView(), this.getFrameconn());
		feastDate=feastDate.replace(" ","");
		String[] dates=feastDate.split(",");
		String year="";
		int len=0;
		for (int i = 0; i < dates.length; i++) {
			String date = dates[i];
			//判断数据重复
			if (holidayStr.indexOf(date)!=-1) {
				ArrayList<String> sb = this.msgMap.get(Integer.valueOf(num));
				if (sb == null)
					sb = new ArrayList<String>();
				String msg = "&nbsp;第" +num + "行" + ResourceFactory.getProperty("kq.search_feast.error.same");
				sb.add((sb.size() + 1) + ".&nbsp;" + msg);
				this.msgMap.put(Integer.valueOf(num), sb);
				continue;
			}
			holidayStr +=","+date;
			boolean flag=isValidDate(date);
			if (!flag) {//日期格式不对
				ArrayList<String> sbList = this.msgMap.get(Integer.valueOf(num));
				if (sbList == null)
					sbList = new ArrayList<String>();
				String msg = "&nbsp;第" +num + "行" + ResourceFactory.getProperty("kq.search_feast.labor");
				sbList.add((sbList.size() + 1) + ".&nbsp;" + msg);
				this.msgMap.put(Integer.valueOf(num), sbList);
				continue;
			}
			if (date.length()==10) {
				String yearString=date.substring(0,4);
				if (!"".equals(year)&&!yearString.equals(year)) {//节假日可以有年份，如果是多个日期，那么年份应一致
					ArrayList<String> sbList = this.msgMap.get(Integer.valueOf(num));
					if (sbList == null)
						sbList = new ArrayList<String>();
					String msg = "&nbsp;第" +num + "行" + ResourceFactory.getProperty("kq.search_feast.error.diff");
					sbList.add((sbList.size() + 1) + ".&nbsp;" + msg);
					this.msgMap.put(Integer.valueOf(num), sbList);
					continue;
				}
				year=yearString;
			}
			if (len==0) {
				len=date.length();
			}else {
				if (len!=date.length()) {//节假日可以没有年份，如果是多个日期，那么是否有年份应一致
					ArrayList<String> sbList = this.msgMap.get(Integer.valueOf(num));
					if (sbList == null)
						sbList = new ArrayList<String>();
					String msg = "&nbsp;第" +num + "行" + ResourceFactory.getProperty("kq.search_feast.error.diff");
					sbList.add((sbList.size() + 1) + ".&nbsp;" + msg);
					this.msgMap.put(Integer.valueOf(num), sbList);
					continue;
				}
			}
			boolean checkflag=kqCalenderService.checkHoliday("", date, "");
			if (!checkflag) {//日期已存在
				ArrayList<String> sbList = this.msgMap.get(Integer.valueOf(num));
				if (sbList == null)
					sbList = new ArrayList<String>();
				String msg = "&nbsp;第" +num + "行" + ResourceFactory.getProperty("kq.search_feast.error.isHave");
				sbList.add((sbList.size() + 1) + ".&nbsp;" + msg);
				this.msgMap.put(Integer.valueOf(num), sbList);
				continue;
			}
		}
		return year;
	}
	public static String is_RestDate2(String cur_date, UserView userView, String rest_date) {
		String rest_state = ResourceFactory.getProperty("kq.date.work");
		String restdate = ResourceFactory.getProperty("kq.date.rest");
		if (IfRestDate.if_Rest(cur_date, userView, rest_date))// 判断公休日
		{
			rest_state = restdate;
		}
		return rest_state;
	}

	private static boolean isValidDate(String strValue) {//2020.01.02
		strValue=strValue.replace(".", "-");
		String[] date=strValue.split("-");
		int day=0;
		int month=0;
		int year=0;
		try {
			if (strValue.length()!=5&&strValue.length()!=10) {
				return false;
			}
			if (date.length==2) {
				day = Integer.parseInt(date[1]);
				month =Integer.parseInt(date[0]);
			}else if (date.length==3) {
				day = Integer.parseInt(date[2]);
				month =Integer.parseInt(date[1]);
				year = Integer.parseInt(date[0]);
			}else {
				return false;
			}
			if (day < 1 || month < 1 || month > 12) {
				return false;
			}

			if (month == 2) {
				if (isLeapYear(year))
					return day <= 29;
				else
					return day <= 28;
			} else if (month == 4 || month == 6 || month == 9 || month == 11) {
				return day <= 30;
			} else {
				return day <= 31;
			}
		} catch (Exception e) {
			 return false;
		}
	}

	private static boolean isLeapYear(int y) {//判断是否为闰年
		return y % 4 == 0 && (y % 400 == 0 || y%100 != 0);
	}
	/**
	 * 获取模板数据的异常信息
	 */
	private String getErrorMsg() {
		StringBuffer errorMsg = new StringBuffer();
		try {
			Iterator<Entry<Integer, ArrayList<String>>> it = this.msgMap.entrySet().iterator();
			while (it.hasNext()) {
				if(errorMsg.length() < 1)
					errorMsg.append("[");
				
				Entry<Integer, ArrayList<String>> entry = it.next();
				int id = entry.getKey();
				ArrayList<String> msgList = entry.getValue();
				errorMsg.append("{id:'" + id + "',message:'");
				for (int i = 0; i < msgList.size(); i++) {
					String msg = msgList.get(i);
					if (i > 0)
						errorMsg.append("<br>");

					errorMsg.append(msg);
				}

				errorMsg.append("'},");
			}

			if (errorMsg.toString().endsWith(","))
				errorMsg.setLength(errorMsg.length() - 1);

		} catch (Exception e) {
			e.printStackTrace();
		}

		if(errorMsg.length() > 1)
			errorMsg.append("]");
		
		return errorMsg.toString();
	}
}
