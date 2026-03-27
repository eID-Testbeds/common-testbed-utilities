package de.bsi.testbedutils.bouncycertgen.xml.POSeIDAS;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class PkiConnectorConfiguration {
    @XmlElement(name = "blackListTrustAnchor")
    private String blackListTrustAnchor;
    @XmlElement(name = "masterListTrustAnchor")
    private String masterListTrustAnchor;
    @XmlElement(name = "autentServerUrl")
    private String autentServerUrl;
    @XmlElement(name = "policyImplementationId")
    private String policyImplementationId;
    @XmlElement(name = "sslKeys")
    private SslKeys sslKeys;
    @XmlElement(name = "terminalAuthService")
    private TerminalAuthService terminalAuthService;
    @XmlElement(name = "restrictedIdService")
    private RestrictedIdService restrictedIdService;
    @XmlElement(name = "passiveAuthService")
    private PassiveAuthService passiveAuthService;
    @XmlElement(name = "dvcaCertDescriptionService")
    private DvcaCertDescriptionService dvcaCertDescriptionService;

    public String getBlackListTrustAnchor() {
        return blackListTrustAnchor;
    }

    public void setBlackListTrustAnchor(String blackListTrustAnchor) {
        this.blackListTrustAnchor = blackListTrustAnchor;
    }

    public String getMasterListTrustAnchor() {
        return masterListTrustAnchor;
    }

    public void setMasterListTrustAnchor(String masterListTrustAnchor) {
        this.masterListTrustAnchor = masterListTrustAnchor;
    }

    public String getAutentServerUrl() {
        return autentServerUrl;
    }

    public void setAutentServerUrl(String autentServerUrl) {
        this.autentServerUrl = autentServerUrl;
    }

    public String getPolicyImplementationId() {
        return policyImplementationId;
    }

    public void setPolicyImplementationId(String policyImplementationId) {
        this.policyImplementationId = policyImplementationId;
    }

    public SslKeys getSslKeys() {
        return sslKeys;
    }

    public void setSslKeys(SslKeys sslKeys) {
        this.sslKeys = sslKeys;
    }

    public TerminalAuthService getTerminalAuthService() {
        return terminalAuthService;
    }

    public void setTerminalAuthService(TerminalAuthService terminalAuthService) {
        this.terminalAuthService = terminalAuthService;
    }

    public RestrictedIdService getRestrictedIdService() {
        return restrictedIdService;
    }

    public void setRestrictedIdService(RestrictedIdService restrictedIdService) {
        this.restrictedIdService = restrictedIdService;
    }

    public PassiveAuthService getPassiveAuthService() {
        return passiveAuthService;
    }

    public void setPassiveAuthService(PassiveAuthService passiveAuthService) {
        this.passiveAuthService = passiveAuthService;
    }

    public DvcaCertDescriptionService getDvcaCertDescriptionService() {
        return dvcaCertDescriptionService;
    }

    public void setDvcaCertDescriptionService(DvcaCertDescriptionService dvcaCertDescriptionService) {
        this.dvcaCertDescriptionService = dvcaCertDescriptionService;
    }
}
