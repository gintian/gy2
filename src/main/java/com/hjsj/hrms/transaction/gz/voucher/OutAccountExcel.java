package com.hjsj.hrms.transaction.gz.voucher;

import com.hjsj.hrms.businessobject.gz.voucher.VoucherBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 
* 
* 类名称：OutAccountExcel   
* 类描述：   
* 创建人：zhaoxg   
* 创建时间：Aug 16, 2013 5:45:36 PM   
* 修改人：zhaoxg   
* 修改时间：Aug 16, 2013 5:45:36 PM   
* 修改备注：   科目导出excel
* @version    
*
 */
public class OutAccountExcel extends IBusiness {

	public void execute() throws GeneralException {
		
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		/* 安全问题 sql-in-url 财务凭证定义-设置 xiaoyun 2014-9-16 start */
		// String sql = SafeCode.decode((String) this.getFormHM().get("sql"));
		String sql = (String)this.userView.getHm().get("gz_sql_1");
		sql=PubFunc.keyWord_reback(sql);
		sql = PubFunc.decrypt(SafeCode.decode(sql));
		/* 安全问题 sql-in-url 财务凭证定义-设置 xiaoyun 2014-9-16 end */
		try {
			VoucherBo vcbo=new VoucherBo(this.getFrameconn(),this.getUserView());
			ArrayList accountlist = vcbo.getAccountList();//列的名字，就是Excel表第一行数据
			ArrayList accountDatalist = vcbo.getAccountDataList(accountlist, sql, dao);//其余各行的数据
//			if(accountDatalist.size()==0)
//			{
//				throw GeneralExceptionHandler.Handle(new Exception("无数据导出!"));
//			}
			String fileName=this.getUserView().getUserName()+PubFunc.getStrg()+".xls";
			String url=System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+fileName;
			vcbo.exportGzData(fileName, accountDatalist, accountlist, url);
			/* 安全问题 文件导出 参悟凭证定义-设置-导出 xiaoyun 2014-9-16 start */
			fileName = SafeCode.encode(PubFunc.encrypt(fileName));
			/* 安全问题 文件导出 参悟凭证定义-设置-导出 xiaoyun 2014-9-16 end */
			this.getFormHM().put("fileName", fileName);
		} catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
