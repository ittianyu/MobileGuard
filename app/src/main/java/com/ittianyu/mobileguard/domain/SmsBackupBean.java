package com.ittianyu.mobileguard.domain;

import java.util.List;

/**
 * Created by yu.
 * use for BackupRestoreEngine
 */
public class SmsBackupBean {
    private List<Data> datas;

    public SmsBackupBean() {
    }

    public SmsBackupBean(List<Data> datas) {
        this.datas = datas;
    }

    public List<Data> getDatas() {
        return datas;
    }

    public void setDatas(List<Data> datas) {
        this.datas = datas;
    }

    @Override
    public String toString() {
        return "SmsBackupBean{" +
                "datas=" + datas +
                '}';
    }

    public static class Data {
        private String key;
        private String value;

        public Data() {
        }

        public Data(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "key='" + key + '\'' +
                    ", value='" + value + '\'' +
                    '}';
        }
    }
}
