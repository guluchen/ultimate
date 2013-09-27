//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.3 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.02.16 at 09:20:11 AM NZDT 
//


package net.sourceforge.czt.oz.jaxb.gen;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import net.sourceforge.czt.z.jaxb.gen.NameTypePair;
import net.sourceforge.czt.z.jaxb.gen.Signature;
import net.sourceforge.czt.z.jaxb.gen.Type2;


/**
 * <p>Java class for ClassType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ClassType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://czt.sourceforge.net/zml}Type2">
 *       &lt;sequence>
 *         &lt;element ref="{http://czt.sourceforge.net/object-z}ClassRefList"/>
 *         &lt;element ref="{http://czt.sourceforge.net/zml}Signature" minOccurs="0"/>
 *         &lt;element ref="{http://czt.sourceforge.net/zml}NameTypePair" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://czt.sourceforge.net/object-z}NameSignaturePair" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ClassType", propOrder = {
    "classes",
    "state",
    "attribute",
    "operation"
})
@XmlSeeAlso({
    ClassRefType.class,
    ClassUnionType.class,
    ClassPolyType.class
})
public class ClassType
    extends Type2
{

    @XmlElementRef(name = "ClassRefList", namespace = "http://czt.sourceforge.net/object-z", type = JAXBElement.class)
    protected JAXBElement<ClassRefList> classes;
    @XmlElementRef(name = "Signature", namespace = "http://czt.sourceforge.net/zml", type = JAXBElement.class)
    protected JAXBElement<Signature> state;
    @XmlElementRef(name = "NameTypePair", namespace = "http://czt.sourceforge.net/zml", type = JAXBElement.class)
    protected List<JAXBElement<NameTypePair>> attribute;
    @XmlElementRef(name = "NameSignaturePair", namespace = "http://czt.sourceforge.net/object-z", type = JAXBElement.class)
    protected List<JAXBElement<NameSignaturePair>> operation;

    /**
     * Gets the value of the classes property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ClassRefList }{@code >}
     *     
     */
    public JAXBElement<ClassRefList> getClasses() {
        return classes;
    }

    /**
     * Sets the value of the classes property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ClassRefList }{@code >}
     *     
     */
    public void setClasses(JAXBElement<ClassRefList> value) {
        this.classes = ((JAXBElement<ClassRefList> ) value);
    }

    /**
     * Gets the value of the state property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Signature }{@code >}
     *     
     */
    public JAXBElement<Signature> getState() {
        return state;
    }

    /**
     * Sets the value of the state property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Signature }{@code >}
     *     
     */
    public void setState(JAXBElement<Signature> value) {
        this.state = ((JAXBElement<Signature> ) value);
    }

    /**
     * Gets the value of the attribute property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the attribute property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAttribute().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link NameTypePair }{@code >}
     * 
     * 
     */
    public List<JAXBElement<NameTypePair>> getAttribute() {
        if (attribute == null) {
            attribute = new ArrayList<JAXBElement<NameTypePair>>();
        }
        return this.attribute;
    }

    /**
     * Gets the value of the operation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the operation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOperation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link NameSignaturePair }{@code >}
     * 
     * 
     */
    public List<JAXBElement<NameSignaturePair>> getOperation() {
        if (operation == null) {
            operation = new ArrayList<JAXBElement<NameSignaturePair>>();
        }
        return this.operation;
    }

}
