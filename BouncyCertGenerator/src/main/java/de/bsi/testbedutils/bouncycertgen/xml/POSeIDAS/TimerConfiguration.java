package de.bsi.testbedutils.bouncycertgen.xml.POSeIDAS;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class TimerConfiguration {
    @XmlElement(name = "certRenewal")
    private TimerConfig certRenewal;

    @XmlElement(name = "blacklistRenewal")
    private TimerConfig blacklistRenewal;

    @XmlElement(name = "masterAndDefectListRenewal")
    private TimerConfig masterAndDefectListRenewal;

    public TimerConfig getCertRenewal() {
        return certRenewal;
    }

    public void setCertRenewal(TimerConfig certRenewal) {
        this.certRenewal = certRenewal;
    }

    public TimerConfig getBlacklistRenewal() {
        return blacklistRenewal;
    }

    public void setBlacklistRenewal(TimerConfig blacklistRenewal) {
        this.blacklistRenewal = blacklistRenewal;
    }

    public TimerConfig getMasterAndDefectListRenewal() {
        return masterAndDefectListRenewal;
    }

    public void setMasterAndDefectListRenewal(TimerConfig masterAndDefectListRenewal) {
        this.masterAndDefectListRenewal = masterAndDefectListRenewal;
    }
}
