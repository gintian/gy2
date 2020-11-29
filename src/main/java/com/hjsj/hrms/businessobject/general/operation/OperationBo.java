package com.hjsj.hrms.businessobject.general.operation;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import org.apache.commons.beanutils.DynaBean;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class OperationBo {
	/*
	 * operationcode =null大类 !=null小类
	 */
	public static String[] getSerial(ContentDAO dao,String operationcode) throws GeneralException, SQLException{
		String[] serialnum =new String[2];
		String sql="select  operationid= max(operationid) from operation";
		RowSet rs =dao.search(sql);
		int operationid=0;
		if(rs.next()){
			operationid=rs.getInt("operationid")+1;
		}
		if(operationcode==null){
			ArrayList serialist=getAllSerial(dao);
			serialnum[1]=(String) serialist.get(0);
		}else{
			ArrayList serialist=getAllSerial(dao,operationcode);
			serialnum[1]=(String) serialist.get(0);
		}
		serialnum[0]=operationid+"";
		return serialnum;		
	}
	public static ArrayList getAllSerial(ContentDAO dao) throws GeneralException{
		/*
		 * 得到大类所有序列号
		 */
		ArrayList serialist =getAllSerial(dao,null);
		return serialist;
		
	}
	public static ArrayList getAllSerial(ContentDAO dao,String operationcode) throws GeneralException{
		ArrayList serialist=new ArrayList();
		if(operationcode==null){
//			产生2位代码
		String sql="select operationcode from operation where operationcode like '__'";
		ArrayList operationlist= dao.searchDynaList(sql);
			for(int i=0;i<10;i++){
				for(int j=1;j<10;j++){
					String serialnum=i+""+j;
					serialist.add(serialnum);
				}
			}
			for(int m=0;m<operationlist.size();m++){
				DynaBean dynabean=(DynaBean) operationlist.get(m);
				String operationc =(String) dynabean.get("operationcode");
				if(serialist.contains(operationc)) {
                    serialist.remove(operationc);
                }
			}
		}else{
//			产生四位代码
			String sql="select operationcode from operation where operationcode like '"+operationcode+"__'";
			ArrayList operationlist= dao.searchDynaList(sql);
				for(int i=0;i<10;i++){
					for(int j=1;j<10;j++){
						String serialnum=operationcode+i+""+j;
						serialist.add(serialnum);
					}
				}
				for(int m=0;m<operationlist.size();m++){
					DynaBean dynabean=(DynaBean) operationlist.get(m);
					String operationc =(String) dynabean.get("operationcode");
					if(serialist.contains(operationc)) {
                        serialist.remove(operationc);
                    }
				}
		}
		
		return serialist;
	}
	public static void addOperationVo(ContentDAO dao , RecordVo operationVo) throws GeneralException{
		dao.addValueObject(operationVo);
	}
	public static void updateOperationVo(ContentDAO dao, RecordVo operationVo) throws GeneralException, SQLException{
		dao.updateValueObject(operationVo);
	}
	public static void delOperationVo(ContentDAO dao, RecordVo operationVo) throws GeneralException, SQLException{
		dao.deleteValueObject(operationVo);
		
	}
	public static int getTemplateSerial(ContentDAO dao) throws GeneralException{
		ArrayList templatSerialnum=dao.searchDynaList("select tabid from template_table union select tabid from t_wf_define ");
		for(int i=1;i<templatSerialnum.size();i++){
			DynaBean dynabean =(DynaBean) templatSerialnum.get(i-1);
			String tabid =(String) dynabean.get("tabid");
			Integer t=new Integer(tabid);
			if(i!=t.intValue()){
				return i;
			}
		}
		return templatSerialnum.size();
		
		
	}

}
