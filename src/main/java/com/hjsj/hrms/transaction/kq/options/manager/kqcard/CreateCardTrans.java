package com.hjsj.hrms.transaction.kq.options.manager.kqcard;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.options.kqcrad.KqCardLength;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.util.ArrayList;

public class CreateCardTrans extends IBusiness {

	public void execute() throws GeneralException {
		ArrayList list = new ArrayList();
		try {
			String id_len_str = (String) this.getFormHM().get("id_len");
			KqCardLength kqCardLength = new KqCardLength(this.getFrameconn());
			int id_len = 0;
			if (id_len_str != null && id_len_str.length() <= 0) {
				id_len = Integer.parseInt(id_len_str);
			} else {
				id_len = kqCardLength.tack_CardLen();
			}
			/*
			 * IDGenerator idg=new IDGenerator(2,this.getFrameconn()); String
			 * card_no=idg.getId("kq_cards.card_no").toUpperCase();
			 */
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			String card_no = kqCardLength.createKqCard(id_len);
			KqParameter kq_paramter = new KqParameter(this.userView, "", this
					.getFrameconn());
			String dbNbases = kq_paramter.getNbase();
			// String kq_type = kq_paramter.getKq_type();
			String kq_cardno = kq_paramter.getCardno();
			String dbnase[] = dbNbases.split(",");
			StringBuffer sql = new StringBuffer();
			if (dbnase != null && dbnase.length > 0) {
				while (true) {
					for (int i = 0; i < dbnase.length; i++) {
						sql.append("UNION SELECT 1 FROM ");
						sql.append(dbnase[i]);
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
						vo_card.setString("status", "1");
						dao.addValueObject(vo_card);
						card_no = kqCardLength.createKqCard(id_len);
					} else {
						break;
					}
				}				
			}
			if (card_no == null || card_no.length() <= 0) {
				throw GeneralExceptionHandler
						.Handle(new GeneralException("", ResourceFactory
								.getProperty("kq.card.nocreate.card_no"), "",
								""));
			}

			try {
				RecordVo vo_card = new RecordVo("kq_cards");
				vo_card.setString("card_no", card_no);
				vo_card.setString("status", "-1");
				dao.addValueObject(vo_card);
			} catch (Exception e) {
				e.printStackTrace();
			}

			CommonData vo = new CommonData();
			vo.setDataName(card_no);
			vo.setDataValue(card_no);
			list.add(vo);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(new GeneralException("",
					ResourceFactory.getProperty("kq.card.nocreate.card_no"),
					"", ""));
		}
		this.getFormHM().put("card_list", list);
	}

}
