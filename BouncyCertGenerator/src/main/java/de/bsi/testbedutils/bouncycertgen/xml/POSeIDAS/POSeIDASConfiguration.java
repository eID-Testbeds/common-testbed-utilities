package de.bsi.testbedutils.bouncycertgen.xml.POSeIDAS;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "CoreConfiguration")
@XmlAccessorType(XmlAccessType.FIELD)
public class POSeIDASConfiguration {

    @XmlAttribute(name = "xmlns")
    private String xmlns = "http:/www.bos_bremen.de/2009/06/eID-Server-CoreConfig";

    @XmlElement(name = "ServerUrl")
    private String serverUrl;

    @XmlElement(name = "sessionManagerUsesDatabase")
    private boolean sessionManagerUsesDatabase;

    @XmlElement(name = "sessionMaxPendingRequests")
    private int sessionMaxPendingRequests;

    @XmlElement(name = "TimerConfiguration")
    private TimerConfiguration timerConfiguration;

    @XmlElement(name = "ServiceProvider")
    private List<ServiceProvider> serviceProviders;

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public boolean isSessionManagerUsesDatabase() {
        return sessionManagerUsesDatabase;
    }

    public void setSessionManagerUsesDatabase(boolean sessionManagerUsesDatabase) {
        this.sessionManagerUsesDatabase = sessionManagerUsesDatabase;
    }

    public int getSessionMaxPendingRequests() {
        return sessionMaxPendingRequests;
    }

    public void setSessionMaxPendingRequests(int sessionMaxPendingRequests) {
        this.sessionMaxPendingRequests = sessionMaxPendingRequests;
    }

    public TimerConfiguration getTimerConfiguration() {
        return timerConfiguration;
    }

    public void setTimerConfiguration(TimerConfiguration timerConfiguration) {
        this.timerConfiguration = timerConfiguration;
    }

    public List<ServiceProvider> getServiceProviders() {
        return serviceProviders;
    }

    public void setServiceProviders(List<ServiceProvider> serviceProviders) {
        this.serviceProviders = serviceProviders;
    }
}
