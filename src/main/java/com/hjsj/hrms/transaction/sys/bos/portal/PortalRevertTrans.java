/**
 * 
 */
package com.hjsj.hrms.transaction.sys.bos.portal;

import com.hjsj.hrms.businessobject.sys.bos.func.FuncMainBo;
import com.hjsj.hrms.utils.FileTypeUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.struts.upload.FormFile;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * <p>Title:查询功能列表</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * @author xgq
 * @version 1.0
 * 
 */
public class PortalRevertTrans extends IBusiness {
    /**
	 */


	
	
	public void execute() throws GeneralException {
		FormFile file = (FormFile) this.getFormHM().get("file");
		InputStream in=null;
		try {
			boolean flag = FileTypeUtil.isFileTypeEqual(file);
			if(!flag){
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("error.common.upload.invalid")));
			}
		    in = file.getInputStream();
			
			Document doc1=null;
			if(this.getFormHM().get("portal_dom")!=null){
				 doc1 = (Document)this.getFormHM().get("portal_dom");
			}else{
				FuncMainBo fbo = new FuncMainBo();
				doc1 =fbo.getDocument();
			}
				Document doc =  PubFunc.generateDom(in);
				//hej 2015-07-08 校验xml文件是否正确
				Element hrp_re = doc1.getRootElement();
				Element hrp = doc.getRootElement();
				if(!hrp.getName().equals(hrp_re.getName())){
					throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("error.common.upload.invalid")));
				}
				//Document doc =  saxbuilder.build(in);
				this.getFormHM().put("portal_dom", doc);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}catch (JDOMException e) {
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}finally
        {
            PubFunc.closeIoResource(in);
        }
   
	} 

}
