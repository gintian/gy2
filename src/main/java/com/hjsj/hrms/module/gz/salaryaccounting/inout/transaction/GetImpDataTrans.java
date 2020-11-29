package com.hjsj.hrms.module.gz.salaryaccounting.inout.transaction;

import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.module.gz.salaryaccounting.inout.businessobject.SalaryInOutBo;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryTemplateBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.virtualfilesystem.service.VfsService;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.io.InputStream;
import java.util.ArrayList;

/**
 *Title:GetImpDataTrans
 *Description:取得上传excel表格数据
 *Company:HJHJ
 *Create time:2015-7-3 
 *@author lis
 */
public class GetImpDataTrans extends IBusiness {

	/**
	 * @author lis
	 * @date 2015-6-29
	 */
	private static final long serialVersionUID = 1L;

	@Override
    public void execute() throws GeneralException {
		try {
			String salaryid=(String)this.getFormHM().get("salaryid");
			String flag = (String)this.getFormHM().get("flag");//1是薪资项目
			
			salaryid=SafeCode.decode(salaryid); //解码
			salaryid =PubFunc.decrypt(salaryid); //解密
			
			SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),this.userView);
			SalaryInOutBo inOutBo = new SalaryInOutBo(this.getFrameconn(), Integer.valueOf(salaryid),this.userView);
			
			if("1".equals(flag)){
				//取得 薪资类别中的薪资项目列表
				ArrayList<LazyDynaBean> aimDataList=inOutBo.getAimDataFieldList(gzbo.getSalaryItemList("initflag!=3", salaryid,1),flag);
				
				this.getFormHM().put("aimDataList",aimDataList);
			}else if("2".equals(flag)){
				//取得 薪资类别中的薪资项目列表
				ArrayList<LazyDynaBean> aimDataList=inOutBo.getAimDataFieldList(gzbo.getSalaryItemList(" codesetid='0'", salaryid,1),flag);
				
				this.getFormHM().put("aimDataListA",aimDataList);
			}else{
				String id = (String) this.getFormHM().get("id");//方案id
				ContentDAO dao = new ContentDAO(this.frameconn);
				if(id==null||id.length()==0){
					DbWizard dbWizard = new DbWizard(this.frameconn);
					if(!dbWizard.isExistTable("gz_relation", false)){//若不存在表则新建
						Table table = inOutBo.getGzRelationTable();
						dbWizard.createTable(table);
						DBMetaModel dbmodel = new DBMetaModel(this.getFrameconn());
						dbmodel.reloadTableModel("gz_relation");
					}else{
						Table table = new Table("gz_relation");
						if (!dbWizard.isExistField("gz_relation", "salaryid",false)) {
							Field temp4 = new Field("salaryid", "薪资类别号");//薪资类别号
							temp4.setDatatype(DataType.INT);
							temp4.setNullable(true);
							temp4.setKeyable(false);
							table.addField(temp4);
							dbWizard.addColumns(table);// 更新列
                            DBMetaModel dbmodel = new DBMetaModel(this.getFrameconn());
                            dbmodel.reloadTableModel("gz_relation");
							table = new Table("gz_relation");
						}
						
						if (!dbWizard.isExistField("gz_relation", "userflag",false)) {
							Field temp4 = new Field("userflag", "用户名");//用户名
							temp4.setDatatype(DataType.STRING);
							temp4.setLength(50);
							temp4.setNullable(true);
							temp4.setKeyable(false);
							table.addField(temp4);
							dbWizard.addColumns(table);// 更新列
						}
					}
					ArrayList list = new ArrayList();
					list.add(salaryid);
					list.add(this.userView.getUserName());
					RowSet rs = dao.search("select * from gz_relation where salaryid=? and userflag=?",list);
					if(rs.next()){
						id = rs.getString("id");
					}
				}
				// 上传组件 vfs改造
	            String fileid = (String)this.getFormHM().get("fileid");
	            InputStream input = VfsService.getFile(fileid);
				/*如果用户没有当前薪资类别的资源权限   20140903  dengcan */
				CheckPrivSafeBo safeBo=new CheckPrivSafeBo(this.getFrameconn(),this.userView);
				safeBo.isSalarySetResource(salaryid.toString(),null);
				
				ArrayList list = gzbo.getSalaryItemList("initflag!=3", salaryid,1);
				/* 取得导入文件中列指标列表  */
				ArrayList<LazyDynaBean> originalDataList=inOutBo.getOriginalDataFiledList(input, id, list);
				
				this.getFormHM().put("originalDataList",originalDataList);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
