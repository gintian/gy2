package com.hjsj.hrms.businessobject.kq.options.kqcrad;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * 本类专门处理
 * <p>
 * Title:KqCardLength.java
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:Jan 9, 2007 10:53:09 AM
 * </p>
 * 
 * @author sunxin
 * @version 1.0
 * 
 */
public class KqCardLength {

	private Connection conn;

	public KqCardLength() {
	}

	public KqCardLength(Connection conn) {
		this.conn = conn;
	}

	/**
	 * 取得卡号长度
	 * 
	 * @return
	 * @throws GeneralException
	 */
	public int getCardLend() throws GeneralException {
		int id_len = 0;
		StringBuffer sql = new StringBuffer();
		sql.append("select id_length");
		sql.append(" from id_factory");
		sql.append(" where sequence_name='kq_cards.card_no'");
		RowSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sql.toString());
			if (rs.next()) {
				String id_length = rs.getString("id_length");
				id_len = Integer.parseInt(id_length);
			}
		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
		}
		return id_len;
	}

	/**
	 * 处理卡号长度异常
	 * 
	 * @return
	 * @throws GeneralException
	 */
	public int tack_CardLen() throws GeneralException {
		int id_len = getCardLend();
		if (id_len <= 0) {
			throw GeneralExceptionHandler
					.Handle(new GeneralException("", ResourceFactory
							.getProperty("kq.card.re.card_len"), "", ""));
		}
		return id_len;
	}

	/**
	 * 设置卡号
	 * 
	 * @param id_len
	 * @throws GeneralException
	 */
	public void setId_Factory(int id_len) throws GeneralException {
		StringBuffer select_sql = new StringBuffer();
		select_sql.append("select id_length");
		select_sql.append(" from id_factory");
		select_sql.append(" where sequence_name='kq_cards.card_no'");
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			int msxValue = getIntMaxvalue(id_len);
			rs = dao.search(select_sql.toString());
			if (rs.next()) {
				/** 修改* */
				update_CardLen(id_len, msxValue);
			} else {
				/** 初始化** */
				initialize_CardLen(id_len, msxValue);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
		}
	}

	/**
	 * 返回int型MaxValue
	 * 
	 * @param id_len
	 * @return
	 */
	private int getIntMaxvalue(int id_len) {
		int maxvalue = 0;
		if (id_len > 10) {
			maxvalue = 2147483647;
		} else {
			maxvalue = (int) Math.pow(10, id_len) - 1;
		}
		return maxvalue;
	}

	/**
	 * 返回Long型MaxValue
	 * 
	 * @param id_len
	 * @return
	 */
	private long getLongMaxvalue(int id_len) {
		long maxvalue = 0;
		if (id_len > 10) {
			maxvalue = 2147483647;
		} else {
			double max_d = Math.pow(10, id_len) - 1;
			maxvalue = Math.round(max_d);
		}
		return maxvalue;
	}

	/**
	 * 初始化卡号
	 * 
	 * @param id_len
	 * @param msxValue
	 */
	private void initialize_CardLen(int id_len, int msxValue) {
		StringBuffer insert_sql = new StringBuffer();
		insert_sql.append("insert into id_factory  ");
		insert_sql.append("(sequence_name, sequence_desc, minvalue, maxvalue,");
		insert_sql.append("auto_increase, increase_order, prefix, suffix, currentid,");
		insert_sql.append("id_length, increment_O)");
		insert_sql.append("values (?,?,?,?,?,?,?,?,?,?,?)");
		ArrayList insert_list = new ArrayList();
		insert_list.add("kq_cards.card_no");
		insert_list.add("考勤卡号");
		insert_list.add("1");
		insert_list.add(msxValue + "");
		insert_list.add("1");
		insert_list.add("1");
		insert_list.add("");
		insert_list.add("");
		insert_list.add("0");
		insert_list.add(id_len + "");
		insert_list.add("1");
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			dao.insert(insert_sql.toString(), insert_list);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 修改卡号长度
	 * 
	 * @param id_len
	 * @param msxValue
	 */
	private void update_CardLen(int id_len, int msxValue) {
		StringBuffer update_sql = new StringBuffer();
		update_sql.append("update id_factory set ");
		update_sql.append(" maxvalue=?,");
		update_sql.append("id_length=? ");
		update_sql.append(" where sequence_name=?");
		ArrayList update_list = new ArrayList();
		update_list.add(msxValue + "");
		update_list.add(id_len + "");
		update_list.add("kq_cards.card_no");
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			dao.update(update_sql.toString(), update_list);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 得到制定位数考勤卡号值大值
	 * 
	 * @param id_len
	 * @return
	 */
	private long getMaxFromKqcards(int id_len) {
		long maxvalue = 0;
		StringBuffer sql = new StringBuffer();
		sql.append("select Max(card_no) as card_no");
		sql.append(" from kq_cards");
		sql.append(" where " + Sql_switcher.length("card_no") + "=" + id_len);
		switch (Sql_switcher.searchDbServer()) {
		case Constant.MSSQL: {
			sql.append(" and card_no <> '' AND card_no IS NOT NULL");
			break;
		}
		case Constant.ORACEL: {
			sql.append(" AND card_no IS NOT NULL");
			break;
		}
		case Constant.DB2: {
			sql.append(" AND card_no IS NOT NULL");
			break;
		}
		}

		RowSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sql.toString());
			if (rs.next()) {
				String card_no = rs.getString("card_no");
				if (card_no == null || card_no.length() <= 0) {
					card_no = "0";
				}
				maxvalue = Long.parseLong(card_no);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
		}
		return maxvalue;
	}

	/**
	 * 创建一个卡号
	 * 
	 * @param id_len
	 */
	public String createKqCard(int id_len) {
		long super_value = getLongMaxvalue(id_len);
		long maxvalue = getMaxFromKqcards(id_len);
		long car_cardno = 0;
		String cur_cardno_str = "";
		if (maxvalue >= super_value) {
			long count = getCountCardno(id_len);
			boolean isCorrest = false;
			String card_no = "";
			do {
				long random = getRandom_Card(id_len, count);
				card_no = fromat_Card(random, id_len);
				isCorrest = check_RandomCard(card_no);
			} while (!isCorrest);
			cur_cardno_str = card_no;
		} else {
			car_cardno = maxvalue + 1;
			cur_cardno_str = fromat_Card(car_cardno, id_len);
		}
		return cur_cardno_str;
	}

	/**
	 * 随即生成的卡号
	 * 
	 * @param id_len
	 * @return
	 */
	private long getRandom_Card(int id_len, long count) {
		long lv = 0;
		double dou_id = Math.pow(10, id_len);
		dou_id = Math.round(dou_id);
		do {
			lv = (long) (Math.random() * dou_id);
		} while (lv <= count);
		return lv;
	}

	/**
	 * 检测一个随技术是否存在
	 * 
	 * @param card_no
	 * @return
	 */
	private boolean check_RandomCard(String card_no) {
		StringBuffer sql = new StringBuffer();
		sql.append("select card_no from kq_cards");
		sql.append(" where card_no='" + card_no + "'");
		ContentDAO dao = new ContentDAO(this.conn);
		boolean isCorrect = true;
		RowSet rs = null;
		try {

			rs = dao.search(sql.toString());
			if (rs.next()) {
				isCorrect = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
		}
		return isCorrect;
	}

	/**
	 * 格式化为对应的位数
	 * 
	 * @param card_no
	 * @param id_len
	 * @return
	 */
	private String fromat_Card(long card_no, int id_len) {
		String card_no_str = card_no + "";
		if (card_no_str.length() < id_len) {
			int remain_num = id_len - card_no_str.length();
			StringBuffer zero_str = new StringBuffer();
			for (int i = 0; i < remain_num; i++) {
				zero_str.append("0");
			}
			card_no_str = zero_str.toString() + card_no_str;
		}
		return card_no_str;
	}

	/**
	 * 得到规定长度卡号纪录个数
	 * 
	 * @param id_len
	 * @return
	 */
	private long getCountCardno(int id_len) {
		StringBuffer sql = new StringBuffer();
		long count = 0;
		sql.append("select COUNT(*)");
		sql.append(" from kq_cards");
		sql.append(" where card_no <> '' AND card_no IS NOT NULL");
		sql.append(" and " + Sql_switcher.length("card_no") + "=" + id_len);
		RowSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sql.toString());
			if (rs.next()) {
				String count_str = rs.getString(1);
				count = Long.parseLong(count_str);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
		}
		return count;
	}

	public ArrayList getCardList(String status, int id_len) {
		StringBuffer sql = new StringBuffer();
		sql.append("select card_no from kq_cards");
		sql.append(" where status='" + status + "'");
		sql.append(" and " + Sql_switcher.length("card_no") + "=" + id_len);
		sql.append(" and " + Sql_switcher.isnull("card_no", "'####'")
				+ "<>'####'");
		ContentDAO dao = new ContentDAO(this.conn);
		ArrayList card_list = new ArrayList();
		RowSet rs = null;
		try {

			rs = dao.search(sql.toString());
			CommonData vo = null;
			vo = new CommonData();
			vo.setDataName("");
			vo.setDataValue("");
			card_list.add(vo);
			while (rs.next()) {
				vo = new CommonData();
				vo.setDataName(rs.getString("card_no"));
				vo.setDataValue(rs.getString("card_no"));
				card_list.add(vo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
		}
		return card_list;
	}

	/**
	 * 新生成考勤卡号
	 * 
	 * @param i
	 * @return
	 */
	public ArrayList createCardsFromIdFac(int i, int id_len, String kq_cardno)
			throws GeneralException {
		//BUG  郑文龙 批量发卡 考号重复
		ArrayList list = new ArrayList();
		ArrayList dbnase = DataDictionary.getDbpreList();
		StringBuffer sql = new StringBuffer();
		ContentDAO dao = new ContentDAO(this.conn);
		for (int r = 0; r < i; r++) {
			try {
				String card_no = createKqCard(id_len);
				try {
					if (dbnase != null && dbnase.size() > 0) {
						while (true) {
							for (int j = 0; j < dbnase.size(); j++) {
								sql.append("UNION SELECT 1 FROM ");
								sql.append(dbnase.get(j));
								sql.append("A01 WHERE ");
								sql.append(kq_cardno);
								sql.append("='" + card_no + "' ");
							}
							sql.delete(0, 6);
							RowSet rs = dao.search(sql.toString());
							sql.setLength(0);
							if (rs.next()) {
								RecordVo vo_card = new RecordVo("kq_cards");
								vo_card.setString("card_no", card_no);
								if (!dao.isExistRecordVo(vo_card)) {
									vo_card.setString("status", "1");
									dao.addValueObject(vo_card);
								} else {
									vo_card.setString("status", "1");
									dao.updateValueObject(vo_card);
								}
								card_no = createKqCard(id_len);
							} else {
								break;
							}
						}
					}
					RecordVo vo_card = new RecordVo("kq_cards");
					vo_card.setString("card_no", card_no);
					vo_card.setString("status", "-1");
					dao.addValueObject(vo_card);
				} catch (Exception e) {
					e.printStackTrace();
				}
				list.add(card_no);
			} catch (Exception e) {
				e.printStackTrace();
				throw GeneralExceptionHandler
						.Handle(new GeneralException("", ResourceFactory
								.getProperty("kq.card.nocreate.card_no"), "",
								""));
			}
		}
		return list;
	}

	/**
	 * 修改考勤卡状态
	 * 
	 * @param card_no
	 * @param status
	 */
	public void upKqCards(String card_no, String status) {
		StringBuffer sql = new StringBuffer();
		sql.append("update kq_cards set ");
		sql.append(" status=? ");
		sql.append(" where card_no=?");
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			ArrayList list = new ArrayList();
			list.add(status);
			list.add(card_no);
			dao.update(sql.toString(), list);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 从考勤卡表中得到一定数量的卡号
	 * 
	 * @param i
	 * @return
	 */
	public ArrayList getCardsListFromKqCards(int i, int id_len, String status) {
		ArrayList list = new ArrayList();
		StringBuffer sql = new StringBuffer();
		sql.append("select card_no from kq_cards");
		sql.append(" where status='" + status + "'");
		sql.append(" and " + Sql_switcher.length("card_no") + "=" + id_len);
		sql.append(" and " + Sql_switcher.isnull("card_no", "'##'") + "<>'##'");
		sql.append(" order by card_no ASC");
		int r = 0;
		RowSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(this.conn);

			try {

				rs = dao.search(sql.toString());
				while (rs.next()) {
					list.add(rs.getString("card_no"));
					r++;
					if (i == r) {
                        break;
                    }
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
		}
		return list;
	}

	/**
	 * 得到一个卡号
	 * 
	 * @param a0100
	 * @param nbase
	 * @param kq_cardno
	 * @return
	 */
	public String getCardOneNo(String a0100, String nbase, String kq_cardno)
			throws GeneralException {
		if (kq_cardno == null || kq_cardno.length() <= 0) {
            return "";
        }
		String sql = "select " + kq_cardno + " as cardno from " + nbase
				+ "A01 where a0100='" + a0100 + "'";
		ContentDAO dao = new ContentDAO(this.conn);
		String cardno = "";
		RowSet rs = null;
		try {
			rs = dao.search(sql);
			if (rs.next()) {
				cardno = rs.getString("cardno");
			}
			if (cardno != null && cardno.length() > 0) {
                return "";
            } else {

				int id_len = tack_CardLen();
				ArrayList list = getCardsListFromKqCards(1, id_len, "-1");
				if (list == null || list.size() <= 0) {
                    list = createCardsFromIdFac(1, id_len, kq_cardno);
                }
				cardno = list.get(0).toString();
				String up = "update kq_cards set status='1' where card_no='"
						+ cardno + "'";
				dao.update(up);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
		}

		return cardno;
	}
}
