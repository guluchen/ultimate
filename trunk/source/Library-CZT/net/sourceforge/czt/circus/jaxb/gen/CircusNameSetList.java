//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.3 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.02.16 at 09:20:11 AM NZDT 
//


package net.sourceforge.czt.circus.jaxb.gen;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import net.sourceforge.czt.circuspatt.jaxb.gen.JokerNameSet;


/**
 * <p>Java class for CircusNameSetList complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CircusNameSetList">
 *   &lt;complexContent>
 *     &lt;extension base="{http://czt.sourceforge.net/circus}NameSetList">
 *       &lt;sequence>
 *         &lt;element ref="{http://czt.sourceforge.net/circus}NameSet" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CircusNameSetList", propOrder = {
    "nameSet"
})
public class CircusNameSetList
    extends NameSetList
{

    @XmlElementRef(name = "NameSet", namespace = "http://czt.sourceforge.net/circus", type = JAXBElement.class)
    protected List<JAXBElement<? extends NameSet>> nameSet;

    /**
     * Gets the value of the nameSet property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the nameSet property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNameSet().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link JokerNameSet }{@code >}
     * {@link JAXBElement }{@code <}{@link NameSet }{@code >}
     * {@link JAXBElement }{@code <}{@link CircusNameSet }{@code >}
     * 
     * 
     */
    public List<JAXBElement<? extends NameSet>> getNameSet() {
        if (nameSet == null) {
            nameSet = new ArrayList<JAXBElement<? extends NameSet>>();
        }
        return this.nameSet;
    }

}
