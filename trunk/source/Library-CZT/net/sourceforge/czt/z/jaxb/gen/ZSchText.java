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
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import net.sourceforge.czt.circus.jaxb.gen.ActionTransformerPred;
import net.sourceforge.czt.circus.jaxb.gen.ProcessTransformerPred;
import net.sourceforge.czt.circus.jaxb.gen.StateUpdate;
import net.sourceforge.czt.circus.jaxb.gen.TransformerPred;
import net.sourceforge.czt.zpatt.jaxb.gen.HeadDeclList;
import net.sourceforge.czt.zpatt.jaxb.gen.JokerDeclList;
import net.sourceforge.czt.zpatt.jaxb.gen.JokerPred;


/**
 * <p>Java class for ZSchText complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ZSchText">
 *   &lt;complexContent>
 *     &lt;extension base="{http://czt.sourceforge.net/zml}SchText">
 *       &lt;sequence>
 *         &lt;element ref="{http://czt.sourceforge.net/zml}DeclList"/>
 *         &lt;element ref="{http://czt.sourceforge.net/zml}Pred" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ZSchText", propOrder = {
    "declList",
    "pred"
})
public class ZSchText
    extends SchText
{

    @XmlElementRef(name = "DeclList", namespace = "http://czt.sourceforge.net/zml", type = JAXBElement.class)
    protected JAXBElement<? extends DeclList> declList;
    @XmlElementRef(name = "Pred", namespace = "http://czt.sourceforge.net/zml", type = JAXBElement.class)
    protected JAXBElement<? extends Pred> pred;

    /**
     * Gets the value of the declList property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link JokerDeclList }{@code >}
     *     {@link JAXBElement }{@code <}{@link DeclList }{@code >}
     *     {@link JAXBElement }{@code <}{@link HeadDeclList }{@code >}
     *     {@link JAXBElement }{@code <}{@link ZDeclList }{@code >}
     *     
     */
    public JAXBElement<? extends DeclList> getDeclList() {
        return declList;
    }

    /**
     * Sets the value of the declList property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link JokerDeclList }{@code >}
     *     {@link JAXBElement }{@code <}{@link DeclList }{@code >}
     *     {@link JAXBElement }{@code <}{@link HeadDeclList }{@code >}
     *     {@link JAXBElement }{@code <}{@link ZDeclList }{@code >}
     *     
     */
    public void setDeclList(JAXBElement<? extends DeclList> value) {
        this.declList = ((JAXBElement<? extends DeclList> ) value);
    }

    /**
     * Gets the value of the pred property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link TransformerPred }{@code >}
     *     {@link JAXBElement }{@code <}{@link ProcessTransformerPred }{@code >}
     *     {@link JAXBElement }{@code <}{@link Fact }{@code >}
     *     {@link JAXBElement }{@code <}{@link FalsePred }{@code >}
     *     {@link JAXBElement }{@code <}{@link Pred }{@code >}
     *     {@link JAXBElement }{@code <}{@link QntPred }{@code >}
     *     {@link JAXBElement }{@code <}{@link TruePred }{@code >}
     *     {@link JAXBElement }{@code <}{@link Exists1Pred }{@code >}
     *     {@link JAXBElement }{@code <}{@link ImpliesPred }{@code >}
     *     {@link JAXBElement }{@code <}{@link OrPred }{@code >}
     *     {@link JAXBElement }{@code <}{@link JokerPred }{@code >}
     *     {@link JAXBElement }{@code <}{@link ForallPred }{@code >}
     *     {@link JAXBElement }{@code <}{@link MemPred }{@code >}
     *     {@link JAXBElement }{@code <}{@link ExprPred }{@code >}
     *     {@link JAXBElement }{@code <}{@link ActionTransformerPred }{@code >}
     *     {@link JAXBElement }{@code <}{@link AndPred }{@code >}
     *     {@link JAXBElement }{@code <}{@link StateUpdate }{@code >}
     *     {@link JAXBElement }{@code <}{@link NegPred }{@code >}
     *     {@link JAXBElement }{@code <}{@link ExistsPred }{@code >}
     *     {@link JAXBElement }{@code <}{@link Pred2 }{@code >}
     *     {@link JAXBElement }{@code <}{@link IffPred }{@code >}
     *     
     */
    public JAXBElement<? extends Pred> getPred() {
        return pred;
    }

    /**
     * Sets the value of the pred property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link TransformerPred }{@code >}
     *     {@link JAXBElement }{@code <}{@link ProcessTransformerPred }{@code >}
     *     {@link JAXBElement }{@code <}{@link Fact }{@code >}
     *     {@link JAXBElement }{@code <}{@link FalsePred }{@code >}
     *     {@link JAXBElement }{@code <}{@link Pred }{@code >}
     *     {@link JAXBElement }{@code <}{@link QntPred }{@code >}
     *     {@link JAXBElement }{@code <}{@link TruePred }{@code >}
     *     {@link JAXBElement }{@code <}{@link Exists1Pred }{@code >}
     *     {@link JAXBElement }{@code <}{@link ImpliesPred }{@code >}
     *     {@link JAXBElement }{@code <}{@link OrPred }{@code >}
     *     {@link JAXBElement }{@code <}{@link JokerPred }{@code >}
     *     {@link JAXBElement }{@code <}{@link ForallPred }{@code >}
     *     {@link JAXBElement }{@code <}{@link MemPred }{@code >}
     *     {@link JAXBElement }{@code <}{@link ExprPred }{@code >}
     *     {@link JAXBElement }{@code <}{@link ActionTransformerPred }{@code >}
     *     {@link JAXBElement }{@code <}{@link AndPred }{@code >}
     *     {@link JAXBElement }{@code <}{@link StateUpdate }{@code >}
     *     {@link JAXBElement }{@code <}{@link NegPred }{@code >}
     *     {@link JAXBElement }{@code <}{@link ExistsPred }{@code >}
     *     {@link JAXBElement }{@code <}{@link Pred2 }{@code >}
     *     {@link JAXBElement }{@code <}{@link IffPred }{@code >}
     *     
     */
    public void setPred(JAXBElement<? extends Pred> value) {
        this.pred = ((JAXBElement<? extends Pred> ) value);
    }

}
