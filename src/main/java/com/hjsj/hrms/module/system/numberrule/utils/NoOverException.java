/*
 *
 *  *   @copyright      Copyright ©  2020 贵州银行 All rights reserved.
 *  *   @project        hrs-backend
 *  *   @author         warne
 *  *   @date           5/30/20, 6:07 PM
 *  *
 *
 */

package com.hjsj.hrms.module.system.numberrule.utils;

/**
 * function：编号用完了
 * datetime：2020-05-30 18:07
 * author：warne
 */
public class NoOverException extends RuntimeException {
    public NoOverException() {
        super();
    }

    public NoOverException(String message) {
        super(message);
    }
}
