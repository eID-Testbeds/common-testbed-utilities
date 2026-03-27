package de.bsi.testbedutils.bouncycertgen.xml.POSeIDAS;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceProvider {
    @XmlAttribute(name = "entityID")
    private String entityID;

    @XmlAttribute(name = "enabled")
    private boolean enabled;

    @XmlElement(name = "EPAConnectorConfiguration")
    private EPAConnectorConfiguration epaConnectorConfiguration;

    public String getEntityID() {
        return entityID;
    }

    public void setEntityID(String entityID) {
        this.entityID = entityID;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public EPAConnectorConfiguration getEpaConnectorConfiguration() {
        return epaConnectorConfiguration;
    }

    public void setEpaConnectorConfiguration(EPAConnectorConfiguration epaConnectorConfiguration) {
        this.epaConnectorConfiguration = epaConnectorConfiguration;
    }
}
