package com.hjsj.hrms.module.system.distributedreporting.generatedata.generatedatabean;

public class SiteBean {
    private String locorgcode;
    private String report_photo;
    private String pkgtype;
    private String import_type;
    private String pkgtime;
    private SuperorgBean superorg;
    private FtpBean ftp;
    private WsdlBean wsdl;
    private String nbase;
    private SendParamBean sendparam;

    public FtpBean getFtp() {
        return ftp;
    }

    public void setFtp(FtpBean ftp) {
        this.ftp = ftp;
    }

    public WsdlBean getWsdl() {
        return wsdl;
    }

    public void setWsdl(WsdlBean wsdl) {
        this.wsdl = wsdl;
    }

    public String getNbase() {
        return nbase;
    }

    public void setNbase(String nbase) {
        this.nbase = nbase;
    }

    public SendParamBean getSendparam() {
        return sendparam;
    }

    public void setSendparam(SendParamBean sendparam) {
        this.sendparam = sendparam;
    }

    public String getLocorgcode() {
        return locorgcode;
    }

    public void setLocorgcode(String locorgcode) {
        this.locorgcode = locorgcode;
    }

    public String getReport_photo() {
        return report_photo;
    }

    public void setReport_photo(String report_photo) {
        this.report_photo = report_photo;
    }

    public String getPkgtype() {
        return pkgtype;
    }

    public void setPkgtype(String pkgtype) {
        this.pkgtype = pkgtype;
    }

    public String getImport_type() {
        return import_type;
    }

    public void setImport_type(String import_type) {
        this.import_type = import_type;
    }

    public String getPkgtime() {
        return pkgtime;
    }

    public void setPkgtime(String pkgtime) {
        this.pkgtime = pkgtime;
    }

    public SuperorgBean getSuperorg() {
        return superorg;
    }

    public void setSuperorg(SuperorgBean superorg) {
        this.superorg = superorg;
    }
}
