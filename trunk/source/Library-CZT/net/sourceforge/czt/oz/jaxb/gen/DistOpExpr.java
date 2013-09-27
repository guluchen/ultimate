//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.3 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.02.16 at 09:20:11 AM NZDT 
//


package net.sourceforge.czt.oz.jaxb.gen;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import net.sourceforge.czt.z.jaxb.gen.SchText;
import net.sourceforge.czt.z.jaxb.gen.ZSchText;


/**
 * <p>Java class for DistOpExpr complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DistOpExpr">
 *   &lt;complexContent>
 *     &lt;extension base="{http://czt.sourceforge.net/object-z}OpExpr">
 *       &lt;sequence>
 *         &lt;element ref="{http://czt.sourceforge.net/zml}SchText"/>
 *         &lt;element ref="{http://czt.sourceforge.net/object-z}OpExpr"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DistOpExpr", propOrder = {
    "schText",
    "opExpr"
})
@XmlSeeAlso({
    DistChoiceOpExpr.class,
    DistSeqOpExpr.class,
    DistConjOpExpr.class
})
public class DistOpExpr
    extends OpExpr
{

    @XmlElementRef(name = "SchText", namespace = "http://czt.sourceforge.net/zml", type = JAXBElement.class)
    protected JAXBElement<? extends SchText> schText;
    @XmlElementRef(name = "OpExpr", namespace = "http://czt.sourceforge.net/object-z", type = JAXBElement.class)
    protected JAXBElement<? extends OpExpr> opExpr;

    /**
     * Gets the value of the schText property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link SchText }{@code >}
     *     {@link JAXBElement }{@code <}{@link ZSchText }{@code >}
     *     
     */
    public JAXBElement<? extends SchText> getSchText() {
        return schText;
    }

    /**
     * Sets the value of the schText property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link SchText }{@code >}
     *     {@link JAXBElement }{@code <}{@link ZSchText }{@code >}
     *     
     */
    public void setSchText(JAXBElement<? extends SchText> value) {
        this.schText = ((JAXBElement<? extends SchText> ) value);
    }

    /**
     * Gets the value of the opExpr property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ExChoiceOpExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link OpPromotionExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link ParallelOpExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link SeqOpExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link DistOpExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link ConjOpExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link DistChoiceOpExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link RenameOpExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link DistConjOpExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link AnonOpExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link HideOpExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link DistSeqOpExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link AssoParallelOpExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link ScopeEnrichOpExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link OpExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link OpExpr2 }{@code >}
     *     
     */
    public JAXBElement<? extends OpExpr> getOpExpr() {
        return opExpr;
    }

    /**
     * Sets the value of the opExpr property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ExChoiceOpExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link OpPromotionExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link ParallelOpExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link SeqOpExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link DistOpExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link ConjOpExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link DistChoiceOpExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link RenameOpExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link DistConjOpExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link AnonOpExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link HideOpExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link DistSeqOpExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link AssoParallelOpExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link ScopeEnrichOpExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link OpExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link OpExpr2 }{@code >}
     *     
     */
    public void setOpExpr(JAXBElement<? extends OpExpr> value) {
        this.opExpr = ((JAXBElement<? extends OpExpr> ) value);
    }

}
