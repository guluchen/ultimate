//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.3 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.02.16 at 09:20:11 AM NZDT 
//


package net.sourceforge.czt.zpatt.jaxb.gen;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import net.sourceforge.czt.circuspatt.jaxb.gen.JokerActionBinding;
import net.sourceforge.czt.circuspatt.jaxb.gen.JokerChannelSetBinding;
import net.sourceforge.czt.circuspatt.jaxb.gen.JokerCommunicationBinding;
import net.sourceforge.czt.circuspatt.jaxb.gen.JokerNameSetBinding;
import net.sourceforge.czt.circuspatt.jaxb.gen.JokerParaBinding;
import net.sourceforge.czt.circuspatt.jaxb.gen.JokerParaListBinding;
import net.sourceforge.czt.circuspatt.jaxb.gen.JokerProcessBinding;


/**
 * <p>Java class for OracleAppl complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OracleAppl">
 *   &lt;complexContent>
 *     &lt;extension base="{http://czt.sourceforge.net/zpatt}Deduction">
 *       &lt;sequence>
 *         &lt;element ref="{http://czt.sourceforge.net/zpatt}Binding" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://czt.sourceforge.net/zpatt}OracleAnswer" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="Name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OracleAppl", propOrder = {
    "binding",
    "oracleAnswer"
})
public class OracleAppl
    extends Deduction
{

    @XmlElementRef(name = "Binding", namespace = "http://czt.sourceforge.net/zpatt", type = JAXBElement.class)
    protected List<JAXBElement<? extends Binding>> binding;
    @XmlElementRef(name = "OracleAnswer", namespace = "http://czt.sourceforge.net/zpatt", type = JAXBElement.class)
    protected JAXBElement<? extends OracleAnswer> oracleAnswer;
    @XmlAttribute(name = "Name")
    protected String name;

    /**
     * Gets the value of the binding property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the binding property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBinding().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link Binding }{@code >}
     * {@link JAXBElement }{@code <}{@link JokerProcessBinding }{@code >}
     * {@link JAXBElement }{@code <}{@link JokerActionBinding }{@code >}
     * {@link JAXBElement }{@code <}{@link JokerExprBinding }{@code >}
     * {@link JAXBElement }{@code <}{@link JokerRenameListBinding }{@code >}
     * {@link JAXBElement }{@code <}{@link JokerNameListBinding }{@code >}
     * {@link JAXBElement }{@code <}{@link JokerParaListBinding }{@code >}
     * {@link JAXBElement }{@code <}{@link JokerCommunicationBinding }{@code >}
     * {@link JAXBElement }{@code <}{@link JokerNameSetBinding }{@code >}
     * {@link JAXBElement }{@code <}{@link JokerExprListBinding }{@code >}
     * {@link JAXBElement }{@code <}{@link JokerParaBinding }{@code >}
     * {@link JAXBElement }{@code <}{@link JokerChannelSetBinding }{@code >}
     * {@link JAXBElement }{@code <}{@link JokerDeclListBinding }{@code >}
     * {@link JAXBElement }{@code <}{@link JokerStrokeBinding }{@code >}
     * {@link JAXBElement }{@code <}{@link JokerNameBinding }{@code >}
     * {@link JAXBElement }{@code <}{@link JokerPredBinding }{@code >}
     * 
     * 
     */
    public List<JAXBElement<? extends Binding>> getBinding() {
        if (binding == null) {
            binding = new ArrayList<JAXBElement<? extends Binding>>();
        }
        return this.binding;
    }

    /**
     * Gets the value of the oracleAnswer property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link OracleAnswer }{@code >}
     *     {@link JAXBElement }{@code <}{@link CheckPassed }{@code >}
     *     
     */
    public JAXBElement<? extends OracleAnswer> getOracleAnswer() {
        return oracleAnswer;
    }

    /**
     * Sets the value of the oracleAnswer property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link OracleAnswer }{@code >}
     *     {@link JAXBElement }{@code <}{@link CheckPassed }{@code >}
     *     
     */
    public void setOracleAnswer(JAXBElement<? extends OracleAnswer> value) {
        this.oracleAnswer = ((JAXBElement<? extends OracleAnswer> ) value);
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

}
