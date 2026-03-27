package de.bsi.testbedutils.bouncycertgen.xml.POSeIDAS;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class TerminalAuthService {
    @XmlAttribute(name = "sslKeysId")
    private String sslKeysId;
    @XmlElement(name = "url")
    private String url;

    public String getSslKeysId() {
        return sslKeysId;
    }

    public void setSslKeysId(String sslKeysId) {
        this.sslKeysId = sslKeysId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
