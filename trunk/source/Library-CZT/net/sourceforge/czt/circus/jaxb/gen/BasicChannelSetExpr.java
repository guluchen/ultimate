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
import net.sourceforge.czt.z.jaxb.gen.Expr;


/**
 * <p>Java class for BasicChannelSetExpr complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BasicChannelSetExpr">
 *   &lt;complexContent>
 *     &lt;extension base="{http://czt.sourceforge.net/zml}Expr">
 *       &lt;sequence>
 *         &lt;element ref="{http://czt.sourceforge.net/circus}CommunicationList"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BasicChannelSetExpr", propOrder = {
    "communicationList"
})
public class BasicChannelSetExpr
    extends Expr
{

    @XmlElementRef(name = "CommunicationList", namespace = "http://czt.sourceforge.net/circus", type = JAXBElement.class)
    protected JAXBElement<? extends CommunicationList> communicationList;

    /**
     * Gets the value of the communicationList property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link CommunicationList }{@code >}
     *     {@link JAXBElement }{@code <}{@link CircusCommunicationList }{@code >}
     *     
     */
    public JAXBElement<? extends CommunicationList> getCommunicationList() {
        return communicationList;
    }

    /**
     * Sets the value of the communicationList property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link CommunicationList }{@code >}
     *     {@link JAXBElement }{@code <}{@link CircusCommunicationList }{@code >}
     *     
     */
    public void setCommunicationList(JAXBElement<? extends CommunicationList> value) {
        this.communicationList = ((JAXBElement<? extends CommunicationList> ) value);
    }

}
