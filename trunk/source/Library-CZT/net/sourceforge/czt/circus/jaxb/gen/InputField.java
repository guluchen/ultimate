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
import net.sourceforge.czt.z.jaxb.gen.AndPred;
import net.sourceforge.czt.z.jaxb.gen.Exists1Pred;
import net.sourceforge.czt.z.jaxb.gen.ExistsPred;
import net.sourceforge.czt.z.jaxb.gen.ExprPred;
import net.sourceforge.czt.z.jaxb.gen.Fact;
import net.sourceforge.czt.z.jaxb.gen.FalsePred;
import net.sourceforge.czt.z.jaxb.gen.ForallPred;
import net.sourceforge.czt.z.jaxb.gen.IffPred;
import net.sourceforge.czt.z.jaxb.gen.ImpliesPred;
import net.sourceforge.czt.z.jaxb.gen.MemPred;
import net.sourceforge.czt.z.jaxb.gen.Name;
import net.sourceforge.czt.z.jaxb.gen.NegPred;
import net.sourceforge.czt.z.jaxb.gen.OrPred;
import net.sourceforge.czt.z.jaxb.gen.Pred;
import net.sourceforge.czt.z.jaxb.gen.Pred2;
import net.sourceforge.czt.z.jaxb.gen.QntPred;
import net.sourceforge.czt.z.jaxb.gen.TruePred;
import net.sourceforge.czt.z.jaxb.gen.ZName;
import net.sourceforge.czt.zpatt.jaxb.gen.JokerName;
import net.sourceforge.czt.zpatt.jaxb.gen.JokerPred;


/**
 * <p>Java class for InputField complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="InputField">
 *   &lt;complexContent>
 *     &lt;extension base="{http://czt.sourceforge.net/circus}Field">
 *       &lt;sequence>
 *         &lt;element ref="{http://czt.sourceforge.net/zml}Name"/>
 *         &lt;element ref="{http://czt.sourceforge.net/zml}Pred"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InputField", propOrder = {
    "variableName",
    "restriction"
})
public class InputField
    extends Field
{

    @XmlElementRef(name = "Name", namespace = "http://czt.sourceforge.net/zml", type = JAXBElement.class)
    protected JAXBElement<? extends Name> variableName;
    @XmlElementRef(name = "Pred", namespace = "http://czt.sourceforge.net/zml", type = JAXBElement.class)
    protected JAXBElement<? extends Pred> restriction;

    /**
     * Gets the value of the variableName property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link JokerName }{@code >}
     *     {@link JAXBElement }{@code <}{@link ZName }{@code >}
     *     {@link JAXBElement }{@code <}{@link Name }{@code >}
     *     
     */
    public JAXBElement<? extends Name> getVariableName() {
        return variableName;
    }

    /**
     * Sets the value of the variableName property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link JokerName }{@code >}
     *     {@link JAXBElement }{@code <}{@link ZName }{@code >}
     *     {@link JAXBElement }{@code <}{@link Name }{@code >}
     *     
     */
    public void setVariableName(JAXBElement<? extends Name> value) {
        this.variableName = ((JAXBElement<? extends Name> ) value);
    }

    /**
     * Gets the value of the restriction property.
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
    public JAXBElement<? extends Pred> getRestriction() {
        return restriction;
    }

    /**
     * Sets the value of the restriction property.
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
    public void setRestriction(JAXBElement<? extends Pred> value) {
        this.restriction = ((JAXBElement<? extends Pred> ) value);
    }

}
