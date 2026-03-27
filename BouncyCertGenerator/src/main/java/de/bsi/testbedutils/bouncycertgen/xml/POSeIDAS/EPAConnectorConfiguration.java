package de.bsi.testbedutils.bouncycertgen.xml.POSeIDAS;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class EPAConnectorConfiguration {
    @XmlAttribute(name = "updateCVC")
    private boolean updateCVC;

    @XmlElement(name = "CVCRefID")
    private String cvcRefId;

    @XmlElement(name = "PkiConnectorConfiguration")
    private PkiConnectorConfiguration pkiConnectorConfiguration;

    @XmlElement(name = "PaosReceiverURL")
    private String paosReceiverURL;

    @XmlElement(name = "hoursRefreshCVCBeforeExpires")
    private int hoursRefreshCVCBeforeExpires;

    public boolean isUpdateCVC() {
        return updateCVC;
    }

    public void setUpdateCVC(boolean updateCVC) {
        this.updateCVC = updateCVC;
    }

    public String getCvcRefId() {
        return cvcRefId;
    }

    public void setCvcRefId(String cvcRefId) {
        this.cvcRefId = cvcRefId;
    }

    public PkiConnectorConfiguration getPkiConnectorConfiguration() {
        return pkiConnectorConfiguration;
    }

    public void setPkiConnectorConfiguration(PkiConnectorConfiguration pkiConnectorConfiguration) {
        this.pkiConnectorConfiguration = pkiConnectorConfiguration;
    }

    public String getPaosReceiverURL() {
        return paosReceiverURL;
    }

    public void setPaosReceiverURL(String paosReceiverURL) {
        this.paosReceiverURL = paosReceiverURL;
    }

    public int getHoursRefreshCVCBeforeExpires() {
        return hoursRefreshCVCBeforeExpires;
    }

    public void setHoursRefreshCVCBeforeExpires(int hoursRefreshCVCBeforeExpires) {
        this.hoursRefreshCVCBeforeExpires = hoursRefreshCVCBeforeExpires;
    }
}
