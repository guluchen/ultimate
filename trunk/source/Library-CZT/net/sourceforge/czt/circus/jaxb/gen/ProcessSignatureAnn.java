//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.3 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.02.16 at 09:20:11 AM NZDT 
//


package net.sourceforge.czt.circus.jaxb.gen;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ProcessSignatureAnn complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ProcessSignatureAnn">
 *   &lt;complexContent>
 *     &lt;extension base="{http://czt.sourceforge.net/circus}CircusAnn">
 *       &lt;sequence>
 *         &lt;element ref="{http://czt.sourceforge.net/circus}ProcessSignature"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProcessSignatureAnn", propOrder = {
    "processSignature"
})
public class ProcessSignatureAnn
    extends CircusAnn
{

    @XmlElementRef(name = "ProcessSignature", namespace = "http://czt.sourceforge.net/circus", type = JAXBElement.class)
    protected JAXBElement<ProcessSignature> processSignature;

    /**
     * Gets the value of the processSignature property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ProcessSignature }{@code >}
     *     
     */
    public JAXBElement<ProcessSignature> getProcessSignature() {
        return processSignature;
    }

    /**
     * Sets the value of the processSignature property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ProcessSignature }{@code >}
     *     
     */
    public void setProcessSignature(JAXBElement<ProcessSignature> value) {
        this.processSignature = ((JAXBElement<ProcessSignature> ) value);
    }

}
