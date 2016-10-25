package com.ittianyu.mobileguard.domain;

import java.util.List;

/**
 * Created by yu.
 * use for BackupRestoreEngine
 */
public class ContactBackupBean {
    private String accountName;
    private String accountType;
    private List<ContactBackupDataBean> datas;

    public ContactBackupBean() {
    }

    public ContactBackupBean(String accountName, String accountType, List<ContactBackupDataBean> datas) {
        this.accountName = accountName;
        this.accountType = accountType;
        this.datas = datas;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public List<ContactBackupDataBean> getDatas() {
        return datas;
    }

    public void setDatas(List<ContactBackupDataBean> datas) {
        this.datas = datas;
    }

    @Override
    public String toString() {
        return "ContactBackupBean{" +
                "accountName='" + accountName + '\'' +
                ", accountType='" + accountType + '\'' +
                ", datas=" + datas +
                '}';
    }

    public static class ContactBackupDataBean {
        private String mimetype;
        private String data;

        public ContactBackupDataBean(String mimetype, String data) {
            this.mimetype = mimetype;
            this.data = data;
        }

        public ContactBackupDataBean() {
        }

        public String getMimetype() {
            return mimetype;
        }

        public void setMimetype(String mimetype) {
            this.mimetype = mimetype;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        @Override
        public String toString() {
            return "ContactBackupDataBean{" +
                    "mimetype='" + mimetype + '\'' +
                    ", data='" + data + '\'' +
                    '}';
        }
    }

}


