//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.3 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.02.16 at 09:20:11 AM NZDT 
//


package net.sourceforge.czt.z.jaxb.gen;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import net.sourceforge.czt.circus.jaxb.gen.ActionPara;
import net.sourceforge.czt.circus.jaxb.gen.ChannelPara;
import net.sourceforge.czt.circus.jaxb.gen.ChannelSetPara;
import net.sourceforge.czt.circus.jaxb.gen.CircusConjPara;
import net.sourceforge.czt.circus.jaxb.gen.NameSetPara;
import net.sourceforge.czt.circus.jaxb.gen.ProcessPara;
import net.sourceforge.czt.circuspatt.jaxb.gen.CircusJokers;
import net.sourceforge.czt.circuspatt.jaxb.gen.JokerPara;
import net.sourceforge.czt.oz.jaxb.gen.ClassPara;
import net.sourceforge.czt.zpatt.jaxb.gen.Jokers;
import net.sourceforge.czt.zpatt.jaxb.gen.RulePara;


/**
 * <p>Java class for Para complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Para">
 *   &lt;complexContent>
 *     &lt;extension base="{http://czt.sourceforge.net/zml}Term">
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Para")
@XmlSeeAlso({
    JokerPara.class,
    CircusJokers.class,
    CircusConjPara.class,
    ProcessPara.class,
    ActionPara.class,
    ChannelSetPara.class,
    ChannelPara.class,
    NameSetPara.class,
    RulePara.class,
    Jokers.class,
    AxPara.class,
    FreePara.class,
    UnparsedPara.class,
    GivenPara.class,
    LatexMarkupPara.class,
    ConjPara.class,
    NarrPara.class,
    OptempPara.class,
    ClassPara.class
})
public class Para
    extends Term
{


}
