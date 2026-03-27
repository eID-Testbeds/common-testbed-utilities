
package de.bsi.testbedutils.bouncycertgen.xml.cv;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse f�r roleType.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="roleType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="CVCA"/>
 *     &lt;enumeration value="DV_DOMESTIC"/>
 *     &lt;enumeration value="DV_FOREIGN"/>
 *     &lt;enumeration value="TERMINAL"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "roleType", namespace = "https://www.bsi.bund.de")
@XmlEnum
public enum RoleType {

    CVCA,
    DV_DOMESTIC,
    DV_FOREIGN,
    TERMINAL;

    public String value() {
        return name();
    }

    public static RoleType fromValue(String v) {
        return valueOf(v);
    }

}
