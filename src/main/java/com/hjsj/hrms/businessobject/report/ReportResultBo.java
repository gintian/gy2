package com.hjsj.hrms.businessobject.report;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.struts.exception.GeneralException;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class ReportResultBo {
	private Connection conn = null;
	private ArrayList colinfolist = null;

	public ReportResultBo(Connection conn) {
		this.conn = conn;
	}

	/**
	 * 获得报表结果集和
	 * 
	 * @param tabid
	 * @param userName
	 * @return
	 */
	public ArrayList getTBxxResultList(String tabid, String userName) {
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet recset = null;
		ArrayList listInfo = new ArrayList();
		DbWizard dbWizard = new DbWizard(this.conn);
		try {
			recset = dao.search("select * from tb" + tabid + " where username='" + userName + "' order by secid");
			ResultSetMetaData rsmd = recset.getMetaData();
			int columnCount = rsmd.getColumnCount();// 得到列数
			int m = 0;
			// if(recset.next()){
			String[] temp3 = new String[columnCount];
			for (int i = 0; i < temp3.length; i++) {
				if (rsmd.getColumnName(i + 1).trim().toUpperCase().startsWith("C")) {
                    m++;
                }
			}
			// }
			// if(dbWizard.isExistField("tb"+tabid, "scopeid",false)){//判断字段是否存在
			// m =m-1;
			// }
			String[] temp2 = new String[m];
			for (int a = 0; a < temp2.length; a++) {
				temp2[a] = "0.0";
			}
			HashSet set = new HashSet();
			while (recset.next()) {
				String[] temp = new String[m];
				set.add(recset.getString("secid"));
				for (int i = 0; i < temp.length; i++) {
					temp[i] = recset.getString("C" + (i + 1));
				}
				listInfo.add(temp);
			}
			if (this.colinfolist != null) {
				if (this.colinfolist.size() > listInfo.size()) {
					for (int k = 1; k <= this.colinfolist.size(); k++) {
						if (set.contains(String.valueOf(k))) {
							continue;
						} else {
							listInfo.add(k - 1, temp2);
						}
					}
				}
			} else {
				try {
					ArrayList rowAndColInfoList;
					TgridBo tgridBo = new TgridBo(conn);
					HashMap rowMap = new HashMap();
					HashMap colMap = new HashMap();
					rowAndColInfoList = tgridBo.getRowAndColInfoList(tgridBo.getGridInfoList(tabid), tabid, rowMap,
							colMap);
					this.colinfolist = (ArrayList) rowAndColInfoList.get(1);
					if (this.colinfolist != null) {
						if (this.colinfolist.size() > listInfo.size()) {
							for (int k = 1; k <= this.colinfolist.size(); k++) {
								if (set.contains(String.valueOf(k))) {
									continue;
								} else {
									listInfo.add(k - 1, temp2);
								}
							}
						}

					}
				} catch (GeneralException e) {
					e.printStackTrace();
				}
			}
			if (rsmd != null) {
                rsmd = null;
            }
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(recset);
		}
		return listInfo;
	}

	/**
	 * 获得报表结果集和(已上报)
	 * 
	 * @param tabid
	 * @param unitcode
	 * @return
	 */
	public ArrayList getTTxxResultList(String tabid, String unitcode) {
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet recset = null;
		ArrayList listInfo = new ArrayList();
		DbWizard dbWizard = new DbWizard(this.conn);
		try {
			boolean existTable = dbWizard.isExistTable("tt_" + tabid, false);
			if(!existTable) {
				throw new Exception("请先进行报表汇总！");
			}
			recset = dao.search("select * from tt_" + tabid + " where unitcode='" + unitcode + "' order by secid");
			ResultSetMetaData rsmd = recset.getMetaData();
			int columnCount = rsmd.getColumnCount();// 得到列数
			int m = 0;
			// if(recset.next()){

			String[] temp3 = new String[columnCount];
			for (int i = 0; i < temp3.length; i++) {
				if (rsmd.getColumnName(i + 1).trim().toUpperCase().startsWith("C")) {
                    m++;
                }
			}
			// }

			String[] temp2 = new String[m];
			for (int a = 0; a < temp2.length; a++) {
				temp2[a] = "0.0";
			}
			HashSet set = new HashSet();
			while (recset.next()) {
				String[] temp = new String[m];
				set.add(recset.getString("secid"));
				for (int i = 0; i < temp.length; i++) {
					temp[i] = recset.getString("C" + (i + 1));
				}
				listInfo.add(temp);
			}
			if (this.colinfolist != null) {
				if (this.colinfolist.size() > listInfo.size()) {
					for (int k = 1; k <= this.colinfolist.size(); k++) {
						if (set.contains(String.valueOf(k))) {
							continue;
						} else {
							listInfo.add(k - 1, temp2);
						}
					}
				}

			} else {
				try {
					ArrayList rowAndColInfoList;
					TgridBo tgridBo = new TgridBo(conn);
					HashMap rowMap = new HashMap();
					HashMap colMap = new HashMap();
					rowAndColInfoList = tgridBo.getRowAndColInfoList(tgridBo.getGridInfoList(tabid), tabid, rowMap,
							colMap);// FIXME 提速
					this.colinfolist = (ArrayList) rowAndColInfoList.get(1);
					if (this.colinfolist != null) {
						if (this.colinfolist.size() > listInfo.size()) {
							for (int k = 1; k <= this.colinfolist.size(); k++) {
								if (set.contains(String.valueOf(k))) {
									continue;
								} else {
									listInfo.add(k - 1, temp2);
								}
							}
						}

					}
				} catch (GeneralException e) {
					e.printStackTrace();
				}
			}
			if (rsmd != null) {
                rsmd = null;
            }
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(recset);
		}
		return listInfo;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

	public ArrayList getColinfolist() {
		return colinfolist;
	}

	public void setColinfolist(ArrayList rowinfolist) {
		this.colinfolist = rowinfolist;
	}

}
