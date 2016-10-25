package com.ittianyu.mobileguard.engine;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Created by yu.
 */
public class PhoneLocationEngineTest {
    @Test
    public void matchPhone() throws Exception {
        PhoneLocationEngine engine = new PhoneLocationEngine();

        PhoneType[] phones = new PhoneType[] {
                new PhoneType("010-1234567", PhoneLocationEngine.PhoneType.TELE),
                new PhoneType("0101234567", PhoneLocationEngine.PhoneType.TELE),
                new PhoneType("17000000000", PhoneLocationEngine.PhoneType.CELL),
                new PhoneType("13000000000", PhoneLocationEngine.PhoneType.CELL),
                new PhoneType("15000000000", PhoneLocationEngine.PhoneType.CELL),
                new PhoneType("0330-12345678-1234", PhoneLocationEngine.PhoneType.UNKNOWN),
                new PhoneType("0330", PhoneLocationEngine.PhoneType.UNKNOWN),
                new PhoneType("12345678901", PhoneLocationEngine.PhoneType.UNKNOWN)
        };
        for (PhoneType phone: phones) {
            PhoneLocationEngine.PhoneType phoneType = engine.matchPhone(phone.getNumber());
            boolean value = (phoneType == phone.getType());
            System.out.println(phone + " actual " + phoneType);
            Assert.assertEquals(true, value);
        }

    }

}

class PhoneType {
    private String number;
    private PhoneLocationEngine.PhoneType type;

    public PhoneType() {
    }

    public PhoneType(String number, PhoneLocationEngine.PhoneType type) {
        this.number = number;
        this.type = type;
    }

    public String getNumber() {
        return number;
    }

    public PhoneLocationEngine.PhoneType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "PhoneType{" +
                "number='" + number + '\'' +
                ", type=" + type +
                '}';
    }
}