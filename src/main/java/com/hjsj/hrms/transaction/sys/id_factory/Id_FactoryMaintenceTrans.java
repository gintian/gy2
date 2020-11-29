package com.hjsj.hrms.transaction.sys.id_factory;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.HashMap;

public class Id_FactoryMaintenceTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		HashMap hm = this.getFormHM();
		RecordVo idvo = new RecordVo("id_factory");
		String updateoraddflag = (String) hm.get("updateoraddflag");
		// 进入修改叶面
		if ("update0".equals(updateoraddflag)) {
			HashMap reqhm = (HashMap) this.getFormHM().get("requestPamaHM");
			String sequence_name = (String) reqhm.get("sequence_name");
			idvo = this.findbyseqname(sequence_name, dao);
			hm.put("idvo", idvo);
			hm.put("updateoraddflag", "update0");
		}
		// 进入增加序列叶面
		if ("add0".equals(updateoraddflag)) {
			if (hm.containsKey("idvo"))
				((RecordVo) hm.get("idvo")).clearValues();

			hm.put("updateoraddflag", "add0");

		}
		// 进入确认修改叶面
		if ("update2".equals(updateoraddflag)) {
			if (validate((RecordVo) (this.getFormHM().get("idvo"))))
				return;
			hm.put("updateoraddflag", "update2");
		}
		// 进行修改操作
		if ("update1".equals(updateoraddflag)) {
			updateid(idvo, hm, dao);
		}
		// 进入增加确认叶面
		if ("add2".equals(updateoraddflag)) {
			if (validate((RecordVo) (this.getFormHM().get("idvo"))))
				return;
			hm.put("updateoraddflag", "add2");
		}
		// 进入增加操作
		if ("add1".equals(updateoraddflag)) {
			addid(idvo, hm, dao);
		}

	}

	public void addid(RecordVo idvo, HashMap hm, ContentDAO dao)
			throws GeneralException {

		idvo = (RecordVo) hm.get("idvo");
		idvo.setInt("status", 1);
		try {
			dao.addValueObject(idvo);
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GeneralExceptionHandler
					.Handle(new GeneralException("", ResourceFactory
							.getProperty("id_factory.exception"), "", ""));
		}
	}

	public void updateid(RecordVo idvo, HashMap hm, ContentDAO dao) {

		idvo = (RecordVo) hm.get("idvo");
		try {
			dao.updateValueObject(idvo);
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public RecordVo findbyseqname(String seqname, ContentDAO dao) {
		RecordVo idvo = new RecordVo("id_factory");
		idvo.setString("sequence_name", seqname);
		try {
			idvo = dao.findByPrimaryKey(idvo);
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return idvo;
	}

	public boolean validate(RecordVo idvo) throws GeneralException {
		boolean flag = false;
		String seqname = (String) idvo.getString("sequence_name");
		String minvalue = (String) idvo.getString("minvalue");
		String maxvalue = (String) idvo.getString("maxvalue");
		String id_length = (String) idvo.getString("id_length");
		String increment_o = (String) idvo.getString("increment_o");
		String currentid=(String) idvo.getString("currentid");
		if (seqname == null || seqname.length() <= 0) {
			flag = true;
			throw GeneralExceptionHandler.Handle(new GeneralException("",
					ResourceFactory.getProperty("id_factory.error.seq_name"),
					"", ""));
		}
		if (minvalue == null || minvalue.length() <= 0) {
			flag = true;
			throw GeneralExceptionHandler.Handle(new GeneralException("",
					ResourceFactory.getProperty("id_factory.error.minvalue"),
					"", ""));
		}
		if (maxvalue == null || maxvalue.length() <= 0) {
			flag = true;
			throw GeneralExceptionHandler.Handle(new GeneralException("",
					ResourceFactory.getProperty("id_factory.error.maxvalue"),
					"", ""));
		}
		if (id_length == null || id_length.length() <= 0) {
			flag = true;
			throw GeneralExceptionHandler.Handle(new GeneralException("",
					ResourceFactory.getProperty("id_factory.error.id_length"),
					"", ""));
		}
		if (increment_o == null || increment_o.length() <= 0) {
			flag = true;
			throw GeneralExceptionHandler.Handle(new GeneralException("",
					ResourceFactory.getProperty("id_factory.error.incream_o"),
					"", ""));
		}
		int minv = 0;
		int maxv = 0;
		int idlength=0;
		int increment=0;
		int currid=0;
		try {
			minv = new Integer(minvalue).intValue();

		} catch (Exception e) {
			
			throw GeneralExceptionHandler.Handle(new GeneralException("",
					ResourceFactory.getProperty("id_factory.error.minparse"),
					"", ""));

		}
		try {
			maxv = new Integer(maxvalue).intValue();

		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(new GeneralException("",
					ResourceFactory.getProperty("id_factory.error.maxparse"),
					"", ""));
		}
		if (minv > maxv) {
			throw GeneralExceptionHandler.Handle(new GeneralException("",
					ResourceFactory.getProperty("id_factory.error.minmax"), "",
					""));
		}
		try{
			idlength=new Integer(id_length).intValue();
		}
		catch(Exception e){
			throw GeneralExceptionHandler.Handle(new GeneralException("",
					ResourceFactory.getProperty("id_factory.error.id_lenparse"), "",
					""));
		}
		try{
			increment=new Integer(increment_o).intValue();
		}
		catch(Exception e){
			throw GeneralExceptionHandler.Handle(new GeneralException("",
					ResourceFactory.getProperty("id_factory.error.id_lenparse"), "",
					""));
		}
		if(currentid!=null&&currentid.length()>=1){
		try{
			currid=new Integer(currentid).intValue();
		}
		catch(Exception e){
			throw GeneralExceptionHandler.Handle(new GeneralException("",
					ResourceFactory.getProperty("id_factory.error.cid"), "",
					""));
		}
		if(currid>maxv||currid<minv){
			throw GeneralExceptionHandler.Handle(new GeneralException("",
					ResourceFactory.getProperty("id_factory.error.cidinmax"), "",
					""));
		}
		}else{
			idvo.setInt("currentid",minv);
			
		}

		return flag;
	}

}
