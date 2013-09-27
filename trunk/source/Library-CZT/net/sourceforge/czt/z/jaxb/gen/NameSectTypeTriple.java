//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.3 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.02.16 at 09:20:11 AM NZDT 
//


package net.sourceforge.czt.z.jaxb.gen;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import net.sourceforge.czt.circus.jaxb.gen.ActionType;
import net.sourceforge.czt.circus.jaxb.gen.ChannelSetType;
import net.sourceforge.czt.circus.jaxb.gen.ChannelType;
import net.sourceforge.czt.circus.jaxb.gen.CircusSigType;
import net.sourceforge.czt.circus.jaxb.gen.CircusType;
import net.sourceforge.czt.circus.jaxb.gen.CommunicationType;
import net.sourceforge.czt.circus.jaxb.gen.NameSetType;
import net.sourceforge.czt.circus.jaxb.gen.ProcessType;
import net.sourceforge.czt.oz.jaxb.gen.ClassPolyType;
import net.sourceforge.czt.oz.jaxb.gen.ClassRefType;
import net.sourceforge.czt.oz.jaxb.gen.ClassType;
import net.sourceforge.czt.oz.jaxb.gen.ClassUnionType;
import net.sourceforge.czt.zpatt.jaxb.gen.JokerName;


/**
 * <p>Java class for NameSectTypeTriple complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="NameSectTypeTriple">
 *   &lt;complexContent>
 *     &lt;extension base="{http://czt.sourceforge.net/zml}Term">
 *       &lt;sequence>
 *         &lt;element ref="{http://czt.sourceforge.net/zml}Name"/>
 *         &lt;element name="Sect" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element ref="{http://czt.sourceforge.net/zml}Type"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NameSectTypeTriple", propOrder = {
    "name",
    "sect",
    "type"
})
public class NameSectTypeTriple
    extends Term
{

    @XmlElementRef(name = "Name", namespace = "http://czt.sourceforge.net/zml", type = JAXBElement.class)
    protected JAXBElement<? extends Name> name;
    @XmlElement(name = "Sect", required = true)
    protected String sect;
    @XmlElementRef(name = "Type", namespace = "http://czt.sourceforge.net/zml", type = JAXBElement.class)
    protected JAXBElement<? extends Type> type;

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link JokerName }{@code >}
     *     {@link JAXBElement }{@code <}{@link ZName }{@code >}
     *     {@link JAXBElement }{@code <}{@link Name }{@code >}
     *     
     */
    public JAXBElement<? extends Name> getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link JokerName }{@code >}
     *     {@link JAXBElement }{@code <}{@link ZName }{@code >}
     *     {@link JAXBElement }{@code <}{@link Name }{@code >}
     *     
     */
    public void setName(JAXBElement<? extends Name> value) {
        this.name = ((JAXBElement<? extends Name> ) value);
    }

    /**
     * Gets the value of the sect property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSect() {
        return sect;
    }

    /**
     * Sets the value of the sect property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSect(String value) {
        this.sect = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link CircusType }{@code >}
     *     {@link JAXBElement }{@code <}{@link ClassRefType }{@code >}
     *     {@link JAXBElement }{@code <}{@link GenParamType }{@code >}
     *     {@link JAXBElement }{@code <}{@link Type2 }{@code >}
     *     {@link JAXBElement }{@code <}{@link ClassUnionType }{@code >}
     *     {@link JAXBElement }{@code <}{@link NameSetType }{@code >}
     *     {@link JAXBElement }{@code <}{@link SchemaType }{@code >}
     *     {@link JAXBElement }{@code <}{@link ActionType }{@code >}
     *     {@link JAXBElement }{@code <}{@link ClassPolyType }{@code >}
     *     {@link JAXBElement }{@code <}{@link GivenType }{@code >}
     *     {@link JAXBElement }{@code <}{@link GenericType }{@code >}
     *     {@link JAXBElement }{@code <}{@link ProcessType }{@code >}
     *     {@link JAXBElement }{@code <}{@link ProdType }{@code >}
     *     {@link JAXBElement }{@code <}{@link PowerType }{@code >}
     *     {@link JAXBElement }{@code <}{@link CircusSigType }{@code >}
     *     {@link JAXBElement }{@code <}{@link Type }{@code >}
     *     {@link JAXBElement }{@code <}{@link CommunicationType }{@code >}
     *     {@link JAXBElement }{@code <}{@link ChannelSetType }{@code >}
     *     {@link JAXBElement }{@code <}{@link ClassType }{@code >}
     *     {@link JAXBElement }{@code <}{@link ChannelType }{@code >}
     *     
     */
    public JAXBElement<? extends Type> getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link CircusType }{@code >}
     *     {@link JAXBElement }{@code <}{@link ClassRefType }{@code >}
     *     {@link JAXBElement }{@code <}{@link GenParamType }{@code >}
     *     {@link JAXBElement }{@code <}{@link Type2 }{@code >}
     *     {@link JAXBElement }{@code <}{@link ClassUnionType }{@code >}
     *     {@link JAXBElement }{@code <}{@link NameSetType }{@code >}
     *     {@link JAXBElement }{@code <}{@link SchemaType }{@code >}
     *     {@link JAXBElement }{@code <}{@link ActionType }{@code >}
     *     {@link JAXBElement }{@code <}{@link ClassPolyType }{@code >}
     *     {@link JAXBElement }{@code <}{@link GivenType }{@code >}
     *     {@link JAXBElement }{@code <}{@link GenericType }{@code >}
     *     {@link JAXBElement }{@code <}{@link ProcessType }{@code >}
     *     {@link JAXBElement }{@code <}{@link ProdType }{@code >}
     *     {@link JAXBElement }{@code <}{@link PowerType }{@code >}
     *     {@link JAXBElement }{@code <}{@link CircusSigType }{@code >}
     *     {@link JAXBElement }{@code <}{@link Type }{@code >}
     *     {@link JAXBElement }{@code <}{@link CommunicationType }{@code >}
     *     {@link JAXBElement }{@code <}{@link ChannelSetType }{@code >}
     *     {@link JAXBElement }{@code <}{@link ClassType }{@code >}
     *     {@link JAXBElement }{@code <}{@link ChannelType }{@code >}
     *     
     */
    public void setType(JAXBElement<? extends Type> value) {
        this.type = ((JAXBElement<? extends Type> ) value);
    }

}
