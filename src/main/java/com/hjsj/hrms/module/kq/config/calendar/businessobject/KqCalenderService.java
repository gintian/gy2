package com.hjsj.hrms.module.kq.config.calendar.businessobject;

import java.util.ArrayList;
import java.util.HashMap;


public interface KqCalenderService {
	/**
	 * 判断节假日日期是否存在
	 * @param date
	 * @return
	 */
		boolean checkHoliday(String id,String date,String kqYear);
		/**
		 * 批量保存节假日
		 * @param holidayList
		 * @return
		 */
		boolean saveHolidayForList(ArrayList<HashMap<String, String>> holidayList);
		/**
		 * 批量保存公休日倒休
		 * @param turnRestList
		 * @return
		 */
		boolean saveTurnRestForList(ArrayList<HashMap<String, String>> turnRestList);
		/**
		 * 更新节假日
		 * @param holidayList
		 * @return
		 */
		boolean updateHolidayForList(ArrayList<HashMap<String, String>> holidayList);
		/**
         * 删除节假日
         * @param holidayList
         * @return
         */
        boolean deleteHolidayForList(ArrayList<HashMap<String, String>> holidayList);
		/**
         * 更新公休日倒休
         * @param turnRestList
         * @return
         */
		boolean updateTurnRestForList(ArrayList<HashMap<String, String>> turnRestList);
		/**
         * 删除公休日倒休
         * @param turnRestList
         * @return
         */
        boolean deleteTurnRestForList(ArrayList<HashMap<String, String>> turnRestList);
		/**
         * 保存公休日
         * @param jsonObj
         * @return
         */
        boolean saveRestWeek(String week);
}
