package com.etong.webclient.actionform.rensi;

import com.hrms.struts.action.*;
import com.hrms.frame.dao.*;
import com.hrms.frame.utility.DateStyle;
import org.apache.struts.action.*;
import javax.servlet.http.*;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: etong</p>
 * @author chenmengqing
 * @version 1.0
 */

public class RensiForm extends FrameForm
{
  public RensiForm()
  {
  }
  private String lx="";
  /**
   * 到职时间及上岗,离岗时间
   */
  private DateStyle first_date=new DateStyle();
  private DateStyle second_date=new DateStyle();
  private DateStyle third_date=new DateStyle();
  private RecordVo bian_vo=new RecordVo("t_rensi_biandong");

  @Override
  public void outPutFormHM()
  {
    this.setBian_vo((RecordVo)this.getFormHM().get("bian_vo"));
//    bian_vo.clearValues();
  }
  @Override
  public void inPutTransHM()
  {
    this.getFormHM().put("bian_vo",bian_vo);
    this.getFormHM().put("first_date",first_date);
    this.getFormHM().put("second_date",second_date);
    this.getFormHM().put("third_date",third_date);
  }

  public RecordVo getBian_vo()
  {
    return bian_vo;
  }
  public void setBian_vo(RecordVo bian_vo)
  {
    this.bian_vo = bian_vo;
  }
  public DateStyle getFirst_date()
  {
    return first_date;
  }
  public DateStyle getSecond_date()
  {
    return second_date;
  }
  public void setFirst_date(DateStyle first_date)
  {
    this.first_date = first_date;
  }
  public void setSecond_date(DateStyle second_date)
  {
    this.second_date = second_date;
  }
  public String getLx()
  {
    return lx;
  }
  public void setLx(String lx)
  {
    this.lx = lx;
  }
  @Override
  public void reset(ActionMapping parm1, HttpServletRequest parm2)
  {
    this.getBian_vo().clearValues();
    this.getBian_vo().setString("change_status","01");
    super.reset(parm1, parm2);
  }
  public DateStyle getThird_date()
  {
    return third_date;
  }
  public void setThird_date(DateStyle third_date)
  {
    this.third_date = third_date;
  }


}