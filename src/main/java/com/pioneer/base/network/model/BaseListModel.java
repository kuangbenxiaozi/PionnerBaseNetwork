package com.pioneer.base.network.model;

import android.text.TextUtils;

import java.util.List;

public abstract class BaseListModel<IM extends BaseListItemModel> extends BaseModel {
    private String da_qid;
    private String da_abtest;
    private String da_ext;
    private String error_no;
    private String error_msg;

    //2.2新添加的错误处理
    private StopService stopservice;

    public StopService getStopservice() {
        return stopservice;
    }

    public void setStopservice(StopService stopservice) {
        this.stopservice = stopservice;
    }

    public String getErrorNo() {
        return error_no;
    }

    public String getErrorMsg() {
        return error_msg;
    }

    /**
     * 用于停服之类的错误信息
     *
     * @return
     */
    public String getErrCode() {
        if (stopservice != null) {
            return stopservice.getErrCode();
        }
        return "";
    }

    /**
     * 用于停服之类的错误信息
     *
     * @return
     */
    public String getErrContent() {
        if (stopservice != null) {
            return stopservice.getContent();
        }
        return "";
    }

    public boolean hasErrCode() {
        if (stopservice != null) {
            return stopservice.hasErrorCode();
        }
        return false;
    }

    public boolean isStopService() {
        if (stopservice != null) {
            if (stopservice.hasErrorCode()) {
                if (stopservice.getErrCode().equals("101") || stopservice.getErrCode().equals("102") || stopservice.getErrCode().equals("104")) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
        return false;
    }


    public abstract List<IM> getDataSet();

    public static class StopService {
        private String errcode; //错误码
        private String content; // 停服公告

        public String getErrCode() {
            return errcode;
        }


        public void setErrCode(String errcode) {
            this.errcode = errcode;
        }


        public String getContent() {
            if (!TextUtils.isEmpty(errcode) && TextUtils.isEmpty(content)) {
                return "服务器做饭去了，马上回来～";
            } else {
                return content;
            }
        }

        public boolean hasErrorCode() {
            if (TextUtils.isEmpty(errcode)) {
                return false;
            } else {
                return true;
            }
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
