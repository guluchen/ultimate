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
import javax.xml.bind.annotation.XmlType;
import net.sourceforge.czt.circus.jaxb.gen.BasicChannelSetExpr;
import net.sourceforge.czt.circus.jaxb.gen.SigmaExpr;
import net.sourceforge.czt.z.jaxb.gen.AndExpr;
import net.sourceforge.czt.z.jaxb.gen.ApplExpr;
import net.sourceforge.czt.z.jaxb.gen.BindExpr;
import net.sourceforge.czt.z.jaxb.gen.BindSelExpr;
import net.sourceforge.czt.z.jaxb.gen.CompExpr;
import net.sourceforge.czt.z.jaxb.gen.CondExpr;
import net.sourceforge.czt.z.jaxb.gen.DecorExpr;
import net.sourceforge.czt.z.jaxb.gen.Exists1Expr;
import net.sourceforge.czt.z.jaxb.gen.ExistsExpr;
import net.sourceforge.czt.z.jaxb.gen.Expr;
import net.sourceforge.czt.z.jaxb.gen.Expr0N;
import net.sourceforge.czt.z.jaxb.gen.Expr1;
import net.sourceforge.czt.z.jaxb.gen.Expr2;
import net.sourceforge.czt.z.jaxb.gen.Expr2N;
import net.sourceforge.czt.z.jaxb.gen.ForallExpr;
import net.sourceforge.czt.z.jaxb.gen.HideExpr;
import net.sourceforge.czt.z.jaxb.gen.IffExpr;
import net.sourceforge.czt.z.jaxb.gen.ImpliesExpr;
import net.sourceforge.czt.z.jaxb.gen.LambdaExpr;
import net.sourceforge.czt.z.jaxb.gen.LetExpr;
import net.sourceforge.czt.z.jaxb.gen.MuExpr;
import net.sourceforge.czt.z.jaxb.gen.Name;
import net.sourceforge.czt.z.jaxb.gen.NegExpr;
import net.sourceforge.czt.z.jaxb.gen.NumExpr;
import net.sourceforge.czt.z.jaxb.gen.OrExpr;
import net.sourceforge.czt.z.jaxb.gen.PipeExpr;
import net.sourceforge.czt.z.jaxb.gen.PowerExpr;
import net.sourceforge.czt.z.jaxb.gen.PreExpr;
import net.sourceforge.czt.z.jaxb.gen.ProdExpr;
import net.sourceforge.czt.z.jaxb.gen.ProjExpr;
import net.sourceforge.czt.z.jaxb.gen.Qnt1Expr;
import net.sourceforge.czt.z.jaxb.gen.QntExpr;
import net.sourceforge.czt.z.jaxb.gen.RefExpr;
import net.sourceforge.czt.z.jaxb.gen.RenameExpr;
import net.sourceforge.czt.z.jaxb.gen.SchExpr;
import net.sourceforge.czt.z.jaxb.gen.SchExpr2;
import net.sourceforge.czt.z.jaxb.gen.SetCompExpr;
import net.sourceforge.czt.z.jaxb.gen.SetExpr;
import net.sourceforge.czt.z.jaxb.gen.ThetaExpr;
import net.sourceforge.czt.z.jaxb.gen.TupleExpr;
import net.sourceforge.czt.z.jaxb.gen.TupleSelExpr;
import net.sourceforge.czt.z.jaxb.gen.ZName;
import net.sourceforge.czt.zpatt.jaxb.gen.JokerExpr;
import net.sourceforge.czt.zpatt.jaxb.gen.JokerName;


/**
 * <p>Java class for OpPromotionExpr complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OpPromotionExpr">
 *   &lt;complexContent>
 *     &lt;extension base="{http://czt.sourceforge.net/object-z}OpExpr">
 *       &lt;sequence>
 *         &lt;element ref="{http://czt.sourceforge.net/zml}Expr" minOccurs="0"/>
 *         &lt;element ref="{http://czt.sourceforge.net/zml}Name"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OpPromotionExpr", propOrder = {
    "expr",
    "name"
})
public class OpPromotionExpr
    extends OpExpr
{

    @XmlElementRef(name = "Expr", namespace = "http://czt.sourceforge.net/zml", type = JAXBElement.class)
    protected JAXBElement<? extends Expr> expr;
    @XmlElementRef(name = "Name", namespace = "http://czt.sourceforge.net/zml", type = JAXBElement.class)
    protected JAXBElement<? extends Name> name;

    /**
     * Gets the value of the expr property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link BindExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link Expr2 }{@code >}
     *     {@link JAXBElement }{@code <}{@link HideExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link ApplExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link TupleSelExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link LetExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link SchExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link IffExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link RenameExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link ProjExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link RefExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link PipeExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link ContainmentExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link QntExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link ProdExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link PredExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link SetExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link ForallExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link SigmaExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link ExistsExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link PolyExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link OrExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link SchExpr2 }{@code >}
     *     {@link JAXBElement }{@code <}{@link ImpliesExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link LambdaExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link SetCompExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link PowerExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link JokerExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link Expr1 }{@code >}
     *     {@link JAXBElement }{@code <}{@link CondExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link TupleExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link BindSelExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link DecorExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link Qnt1Expr }{@code >}
     *     {@link JAXBElement }{@code <}{@link ClassUnionExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link Expr }{@code >}
     *     {@link JAXBElement }{@code <}{@link Exists1Expr }{@code >}
     *     {@link JAXBElement }{@code <}{@link NegExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link MuExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link AndExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link ThetaExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link Expr2N }{@code >}
     *     {@link JAXBElement }{@code <}{@link NumExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link PreExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link CompExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link BasicChannelSetExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link Expr0N }{@code >}
     *     
     */
    public JAXBElement<? extends Expr> getExpr() {
        return expr;
    }

    /**
     * Sets the value of the expr property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link BindExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link Expr2 }{@code >}
     *     {@link JAXBElement }{@code <}{@link HideExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link ApplExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link TupleSelExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link LetExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link SchExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link IffExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link RenameExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link ProjExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link RefExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link PipeExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link ContainmentExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link QntExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link ProdExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link PredExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link SetExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link ForallExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link SigmaExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link ExistsExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link PolyExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link OrExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link SchExpr2 }{@code >}
     *     {@link JAXBElement }{@code <}{@link ImpliesExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link LambdaExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link SetCompExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link PowerExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link JokerExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link Expr1 }{@code >}
     *     {@link JAXBElement }{@code <}{@link CondExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link TupleExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link BindSelExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link DecorExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link Qnt1Expr }{@code >}
     *     {@link JAXBElement }{@code <}{@link ClassUnionExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link Expr }{@code >}
     *     {@link JAXBElement }{@code <}{@link Exists1Expr }{@code >}
     *     {@link JAXBElement }{@code <}{@link NegExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link MuExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link AndExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link ThetaExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link Expr2N }{@code >}
     *     {@link JAXBElement }{@code <}{@link NumExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link PreExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link CompExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link BasicChannelSetExpr }{@code >}
     *     {@link JAXBElement }{@code <}{@link Expr0N }{@code >}
     *     
     */
    public void setExpr(JAXBElement<? extends Expr> value) {
        this.expr = ((JAXBElement<? extends Expr> ) value);
    }

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

}
