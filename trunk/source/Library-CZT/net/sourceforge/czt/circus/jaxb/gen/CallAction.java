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
import net.sourceforge.czt.z.jaxb.gen.ExprList;
import net.sourceforge.czt.z.jaxb.gen.Name;
import net.sourceforge.czt.z.jaxb.gen.ZExprList;
import net.sourceforge.czt.z.jaxb.gen.ZName;
import net.sourceforge.czt.zpatt.jaxb.gen.JokerExprList;
import net.sourceforge.czt.zpatt.jaxb.gen.JokerName;


/**
 * <p>Java class for CallAction complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CallAction">
 *   &lt;complexContent>
 *     &lt;extension base="{http://czt.sourceforge.net/circus}CircusAction">
 *       &lt;sequence>
 *         &lt;element ref="{http://czt.sourceforge.net/zml}Name"/>
 *         &lt;element ref="{http://czt.sourceforge.net/zml}ExprList"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CallAction", propOrder = {
    "name",
    "exprList"
})
public class CallAction
    extends CircusAction
{

    @XmlElementRef(name = "Name", namespace = "http://czt.sourceforge.net/zml", type = JAXBElement.class)
    protected JAXBElement<? extends Name> name;
    @XmlElementRef(name = "ExprList", namespace = "http://czt.sourceforge.net/zml", type = JAXBElement.class)
    protected JAXBElement<? extends ExprList> exprList;

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
     * Gets the value of the exprList property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ZExprList }{@code >}
     *     {@link JAXBElement }{@code <}{@link ExprList }{@code >}
     *     {@link JAXBElement }{@code <}{@link JokerExprList }{@code >}
     *     
     */
    public JAXBElement<? extends ExprList> getExprList() {
        return exprList;
    }

    /**
     * Sets the value of the exprList property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ZExprList }{@code >}
     *     {@link JAXBElement }{@code <}{@link ExprList }{@code >}
     *     {@link JAXBElement }{@code <}{@link JokerExprList }{@code >}
     *     
     */
    public void setExprList(JAXBElement<? extends ExprList> value) {
        this.exprList = ((JAXBElement<? extends ExprList> ) value);
    }

}
