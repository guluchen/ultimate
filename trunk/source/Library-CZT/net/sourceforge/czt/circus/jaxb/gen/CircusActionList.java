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
import net.sourceforge.czt.circuspatt.jaxb.gen.JokerAction;


/**
 * <p>Java class for CircusActionList complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CircusActionList">
 *   &lt;complexContent>
 *     &lt;extension base="{http://czt.sourceforge.net/circus}ActionList">
 *       &lt;sequence>
 *         &lt;element ref="{http://czt.sourceforge.net/circus}CircusAction" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CircusActionList", propOrder = {
    "circusAction"
})
public class CircusActionList
    extends ActionList
{

    @XmlElementRef(name = "CircusAction", namespace = "http://czt.sourceforge.net/circus", type = JAXBElement.class)
    protected List<JAXBElement<? extends CircusAction>> circusAction;

    /**
     * Gets the value of the circusAction property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the circusAction property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCircusAction().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link IntChoiceAction }{@code >}
     * {@link JAXBElement }{@code <}{@link SubstitutionAction }{@code >}
     * {@link JAXBElement }{@code <}{@link LetAction }{@code >}
     * {@link JAXBElement }{@code <}{@link AlphabetisedParallelAction }{@code >}
     * {@link JAXBElement }{@code <}{@link IntChoiceActionIte }{@code >}
     * {@link JAXBElement }{@code <}{@link CircusAction }{@code >}
     * {@link JAXBElement }{@code <}{@link ExtChoiceActionIte }{@code >}
     * {@link JAXBElement }{@code <}{@link ActionIte }{@code >}
     * {@link JAXBElement }{@code <}{@link CircusGuardedCommand }{@code >}
     * {@link JAXBElement }{@code <}{@link LetMuAction }{@code >}
     * {@link JAXBElement }{@code <}{@link IfGuardedCommand }{@code >}
     * {@link JAXBElement }{@code <}{@link PrefixingAction }{@code >}
     * {@link JAXBElement }{@code <}{@link SpecStmtCommand }{@code >}
     * {@link JAXBElement }{@code <}{@link CircusCommand }{@code >}
     * {@link JAXBElement }{@code <}{@link InterleaveAction }{@code >}
     * {@link JAXBElement }{@code <}{@link SkipAction }{@code >}
     * {@link JAXBElement }{@code <}{@link MuAction }{@code >}
     * {@link JAXBElement }{@code <}{@link Action1 }{@code >}
     * {@link JAXBElement }{@code <}{@link ParActionIte }{@code >}
     * {@link JAXBElement }{@code <}{@link Action2 }{@code >}
     * {@link JAXBElement }{@code <}{@link ExtChoiceAction }{@code >}
     * {@link JAXBElement }{@code <}{@link ChaosAction }{@code >}
     * {@link JAXBElement }{@code <}{@link VarDeclCommand }{@code >}
     * {@link JAXBElement }{@code <}{@link AlphabetisedParallelActionIte }{@code >}
     * {@link JAXBElement }{@code <}{@link CallAction }{@code >}
     * {@link JAXBElement }{@code <}{@link SchExprAction }{@code >}
     * {@link JAXBElement }{@code <}{@link AssignmentCommand }{@code >}
     * {@link JAXBElement }{@code <}{@link StopAction }{@code >}
     * {@link JAXBElement }{@code <}{@link ParallelActionIte }{@code >}
     * {@link JAXBElement }{@code <}{@link SeqAction }{@code >}
     * {@link JAXBElement }{@code <}{@link SeqActionIte }{@code >}
     * {@link JAXBElement }{@code <}{@link ActionD }{@code >}
     * {@link JAXBElement }{@code <}{@link BasicAction }{@code >}
     * {@link JAXBElement }{@code <}{@link HideAction }{@code >}
     * {@link JAXBElement }{@code <}{@link DoGuardedCommand }{@code >}
     * {@link JAXBElement }{@code <}{@link ParamAction }{@code >}
     * {@link JAXBElement }{@code <}{@link ParAction }{@code >}
     * {@link JAXBElement }{@code <}{@link InterleaveActionIte }{@code >}
     * {@link JAXBElement }{@code <}{@link ParallelAction }{@code >}
     * {@link JAXBElement }{@code <}{@link JokerAction }{@code >}
     * {@link JAXBElement }{@code <}{@link GuardedAction }{@code >}
     * {@link JAXBElement }{@code <}{@link LetVarAction }{@code >}
     * 
     * 
     */
    public List<JAXBElement<? extends CircusAction>> getCircusAction() {
        if (circusAction == null) {
            circusAction = new ArrayList<JAXBElement<? extends CircusAction>>();
        }
        return this.circusAction;
    }

}
