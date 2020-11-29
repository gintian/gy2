package com.hjsj.hrms.transaction.general.inform.multimedia;

import com.hjsj.hrms.businessobject.infor.multimedia.MultiMediaBo;
import com.hjsj.hrms.businessobject.structuresql.MyselfDataApprove;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * <p>Title:SearchMultimediaTrans.java</p>
 * <p>Description>:</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2014-4-25 上午09:40:49</p>
 * <p>@author:wangrd</p>
 * <p>@version: 6.0</p>
 */
public class SearchMultimediaTrans extends IBusiness {

	public  void execute()throws GeneralException
	{
		String unit="";
		String pos="";
		String a0101="";
		StringBuffer strsql=new StringBuffer();
		try
		{			
			ContentDAO dao = new ContentDAO(this.getFrameconn());
            HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM"); 
            String multimediaflag = (String)hm.get("multimediaflag");
            hm.remove("multimediaflag");
            if(multimediaflag==null){
                multimediaflag = (String)this.getFormHM().get("multimediaflag");
                if(multimediaflag==null){                    
                    multimediaflag = "";
                }
            }
            this.getFormHM().put("multimediaflag",multimediaflag);    
            this.getFormHM().put("filetype",multimediaflag); 
            
            String dbflag = (String)this.getFormHM().get("dbflag");
            //获取筛选条件的人员库前缀
            String nbase = (String)this.getFormHM().get("nbase");
            String A0100 = (String)this.getFormHM().get("a0100");
            String I9999 = (String)this.getFormHM().get("i9999");
            String setid = (String)this.getFormHM().get("setid");
            String canedit = (String)this.getFormHM().get("canedit");
            String sequence = (String)this.getFormHM().get("sequence");
            String state = (String)this.getFormHM().get("state");
            
            I9999 = I9999==null || I9999.length()<1?"0":I9999;
            HashMap rm = (HashMap)this.getFormHM().get("requestPamaHM");
            String encript = (String)rm.get("encript");
            rm.remove("encript");
            //信息审核页面  查看点击左侧树上的分类查看附件 需要解密，故添加按nbase的长度的判断，超过3位则需要解密  chenxg 2017-04-24
            if("true".equals(encript) || (StringUtils.isNotEmpty(nbase) && nbase.length() > 3)){
            		A0100 = PubFunc.decrypt(A0100);
            		A0100 = A0100.substring(1);
            		A0100 = SafeCode.decode(A0100);
            		A0100 = PubFunc.convert64BaseToString(A0100);
            		nbase = PubFunc.decrypt(nbase);
            }
            //当登录用户的a0100和查看的人员的a0100一致时，默认为是查看自己的子集附件，不在校验权限
            if(!A0100.equals(this.userView.getA0100())) {
            	CheckPrivSafeBo checkPiv = new CheckPrivSafeBo(this.frameconn, this.userView);
            	nbase = checkPiv.checkDb(nbase);
            	A0100 = checkPiv.checkA0100("", nbase, A0100, "");
            }
            
            ArrayList multimedialist = new ArrayList();
            MultiMediaBo multiMediaBo = null;
            //信息审核：信息变动表和子集数据对应改为用guidkey对应，因此i9999有可能取到的是guidkey，因此加上是否是数字的校验
            if(StringUtils.isNumeric(I9999)) {
            	if(I9999.indexOf("-") == -1) {
            		I9999 = PubFunc.validateNum(I9999,4)?I9999:PubFunc.decrypt(I9999);
            		multiMediaBo = new MultiMediaBo(this.frameconn,this.userView,
            				dbflag,nbase,setid,A0100,Integer.parseInt(I9999));
            	} else {
            		multiMediaBo = new MultiMediaBo(this.frameconn,this.userView,
            				dbflag,nbase,setid,A0100,I9999);
            	}
            } else {
            	multiMediaBo = new MultiMediaBo(this.frameconn,this.userView,
            			dbflag,nbase,setid,A0100,I9999);
            }
            boolean hasRecord = true;
            
            if(("selfedit".equals(canedit) || "appview".equals(canedit)) && ("insert".equals(state) || "new".equals(state)))
            	     hasRecord = false;
            
            if(hasRecord){
				multimedialist=multiMediaBo.getMultimediaListByKey(multimediaflag);
            }
			
			mulMeKey:if("selfedit".equals(canedit) || "appview".equals(canedit)){
				//如果为编辑状态，并且已经有报批的记录了，不允许编辑，将状态置为预览
				if("selfedit".equals(canedit) && (sequence==null || sequence.length()<1)){
					MyselfDataApprove mysel = new MyselfDataApprove(this.frameconn,this.userView,nbase,A0100);
					hasRecord = mysel.hasRecord(setid, I9999, null, "02");
					if(hasRecord)
						this.getFormHM().put("canedit", "appview");
					break mulMeKey;
				}
				
				if(sequence==null || sequence.length()<1)
					break mulMeKey;
				
				MyselfDataApprove mysel = null;
				if("selfedit".equals(canedit)){
					mysel = new MyselfDataApprove(this.frameconn,this.userView,nbase,A0100);
				}else{
					String chg_id = (String)this.getFormHM().get("chg_id");
					chg_id = PubFunc.decrypt(chg_id);
					mysel = new MyselfDataApprove(this.frameconn,
							this.userView,chg_id);
					mysel.setChg_id(chg_id);
				}
				if("A01".equals(setid))
					I9999 = A0100;
				
				ArrayList mediaInfo= mysel.getMultiMediaInfo(setid,I9999,sequence);
				if(StringUtils.isEmpty(multimediaflag))
				    multimedialist.addAll(mediaInfo);
				else if(mediaInfo != null && mediaInfo.size() > 0){
				    for(int i = 0; i < mediaInfo.size(); i++){
				        LazyDynaBean bean = (LazyDynaBean) mediaInfo.get(i);
				        String flag = (String) bean.get("classId");
				        if(StringUtils.isNotEmpty(flag) && flag.equalsIgnoreCase(multimediaflag))
				            multimedialist.add(bean);
				    }
				}
			}
			this.getFormHM().put("multimedialist", multimedialist);
			this.getFormHM().put("mainguid", multiMediaBo.getMainGuid());
			this.getFormHM().put("childguid", multiMediaBo.getChildGuid());
			
			if("A".equals(dbflag))  // 人员
			{
				strsql.append("select b0110,e0122,e01a1,a0101 from ");
			    strsql.append(nbase);
			    strsql.append("A01 where a0100='");
			    strsql.append(A0100);
			    strsql.append("'");
			    this.frowset = dao.search(strsql.toString()); 
			    if(this.frowset.next())
				{
			    	unit=this.getFrowset().getString("B0110");
			    	pos=this.getFrowset().getString("E0122");
				    a0101=this.getFrowset().getString("a0101");			
				}
			    if(unit !=null && unit.trim().length()>0)
			 		unit=AdminCode.getCode("UN",unit)!=null?AdminCode.getCode("UN",unit).getCodename():"";
				if(pos !=null && pos.trim().length()>0)
					pos=AdminCode.getCode("UM",pos)!=null?AdminCode.getCode("UM",pos).getCodename():"";
			
				this.getFormHM().put("unit",unit);
				this.getFormHM().put("pos",pos);	
				this.getFormHM().put("a0101",a0101);
			}
	
			
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}


	}
	

	
	public String getString(String t_flag)
	{
		StringBuffer ret = new StringBuffer();
		String[] temp = t_flag.split(","); 
		for(int i=0;i<temp.length;i++)
		{
			temp[i] = "'"+temp[i]+"'";
		}
		for(int i=0;i<temp.length;i++)
		{
			ret.append(","+temp[i]);
		}
		return ret.substring(1).toString();
	}
	
}
