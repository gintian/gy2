package com.hjsj.hrms.module.system.filepathsetting.businessobject;

public interface FileMoveService {
    String fileMove();
    String fileRecovery() throws Exception;
    String queryMoveProgress();
    String queryRecoveryProgress();
}
