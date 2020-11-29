/**
 * 
 */
package com.hjsj.hrms.transaction.general.muster.struct;

import com.hjsj.hrms.businessobject.general.muster.MusterBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * <p>
 * Title:MoveRecordTrans
 * </p>
 * <p>
 * Description:移动记录
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2006-4-25:14:42:31
 * </p>
 * 
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class MoveRecordTrans extends IBusiness {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		ContentDAO dao=new ContentDAO(this.getFrameconn()); 
		try {
			/** 信息群标识 */
			String infor = (String) this.getFormHM().get("infor");
			/** 主键值 */
			String keyno = (String) this.getFormHM().get("keyno");
			/** 选中记录的序号 */
			String recidx = (String) this.getFormHM().get("recidx");
			recidx=recidx!=null&&recidx.length()>0?recidx:"1";
			
			MusterBo musterBo=new MusterBo(this.frameconn,this.userView);
			/** 移至目标序号 */
			String dec_idx = (String) this.getFormHM().get("dec_idx");
			dec_idx=dec_idx!=null&&dec_idx.length()>0?dec_idx:"1";
			String setname = (String) this.getFormHM().get("setname");
			
			int maxRecidx=musterBo.getMaxRecidx(setname);
			if((maxRecidx)<Integer.parseInt(dec_idx))
				dec_idx=String.valueOf(maxRecidx);
			
			/*取消主键*/
			
			ArrayList fieldlist=musterBo.getMusterFields(setname.substring(1,setname.indexOf("_")),infor);
			Table table=new Table(setname.toString());
			for(int i=0;i<fieldlist.size();i++)
				table.addField((Field)fieldlist.get(i));
			DbWizard dbWizard=new DbWizard(this.frameconn); 
			dbWizard.dropPrimaryKey(table);
			
			String key_field = null;
			if (infor == null || "".equals(infor))
				infor = "1";
			if ("2".equals(infor))
				key_field = "B0110";
			else if ("3".equals(infor))
				key_field = "E01A1";
			else
				key_field = "A0100";
			int i_src_idx = Integer.parseInt(recidx);
			int i_dec_idx = Integer.parseInt(dec_idx);
			
			dao.update("update "+setname+" set recidx=-1  where recidx="+recidx);
			
			StringBuffer strsql = new StringBuffer();
			strsql.append("update ");
			strsql.append(setname);
		//	strsql.append(" set recidx=recidx+1 where ");
			if (i_dec_idx >i_src_idx)
			{
				strsql.append(" set recidx=recidx-1 where ");
				strsql.append(" recidx>"+i_src_idx);
				strsql.append(" and recidx<="+i_dec_idx);
			}
			else
			{
				strsql.append(" set recidx=recidx+1 where ");
				strsql.append(" recidx<"+i_src_idx);
				strsql.append(" and recidx>="+i_dec_idx);
				
			}
		/*	if (i_dec_idx < i_src_idx) {
				strsql.append(" recidx>=");
				strsql.append(i_dec_idx);
				strsql.append(" and recidx<");
				strsql.append(i_src_idx);
			} else {
				strsql.append(" recidx>=");
				strsql.append(i_dec_idx);
			}
			*/
			dao.update(strsql.toString());
			/***/
			strsql.setLength(0);
			strsql.append("update ");
			strsql.append(setname);
			strsql.append(" set recidx=");
			strsql.append(dec_idx);
			strsql.append(" where recidx=-1");
			
			dao.update(strsql.toString());
			
			//添加主键
			dbWizard.addPrimaryKey(table);
//			musterBo.updateMusterRecidx(setname.toString());
			
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}

	}

}
