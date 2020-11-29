package com.hjsj.hrms.transaction.sys.busimaintence;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class SaveConstructCodesetTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
//		String[] right_fields=(String[])this.getFormHM().get("right_fields");
//		String[] left_fields=(String[])this.getFormHM().get("left_fields");
//		String id=(String)this.getFormHM().get("id");
//		String updatesql="update t_hr_busitable set useflag=? where fieldsetid=? and id='"+id+"'";
//		ArrayList list=new ArrayList();
//		try
//		{
//			ArrayList onelist=new ArrayList();
//			if(right_fields!=null&&right_fields.length>0)
//			{
//				
//				for(int i=0;i<right_fields.length;i++)
//				{
//					onelist=new ArrayList();					
//					onelist.add("1");
//					onelist.add(right_fields[i]);
//					list.add(onelist);
//				}
//			}
//			if(left_fields!=null&&left_fields.length>0)
//			{
//				for(int i=0;i<left_fields.length;i++)
//				{
//					onelist=new ArrayList();					
//					onelist.add("0");
//					onelist.add(left_fields[i]);
//					list.add(onelist);
//				}
//			}
//			ContentDAO dao=new ContentDAO(this.getFrameconn());
//			dao.batchUpdate(updatesql, list);
//		}catch(Exception e)
//		{
//			e.printStackTrace();
//		}
		
		
	}

}
