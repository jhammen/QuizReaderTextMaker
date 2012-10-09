
/* First created by JCasGen Wed Jun 13 14:33:29 MDT 2012 */
package org.quizreader.textmaker.uima.types;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** 
 * Updated by JCasGen Mon Oct 08 20:16:42 MDT 2012
 * @generated */
public class WiktAnnotation_Type extends Annotation_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (WiktAnnotation_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = WiktAnnotation_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new WiktAnnotation(addr, WiktAnnotation_Type.this);
  			   WiktAnnotation_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new WiktAnnotation(addr, WiktAnnotation_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = WiktAnnotation.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("org.quizreader.textmaker.uima.types.WiktAnnotation");
 
  /** @generated */
  final Feature casFeat_Excerpt;
  /** @generated */
  final int     casFeatCode_Excerpt;
  /** @generated */ 
  public String getExcerpt(int addr) {
        if (featOkTst && casFeat_Excerpt == null)
      jcas.throwFeatMissing("Excerpt", "org.quizreader.textmaker.uima.types.WiktAnnotation");
    return ll_cas.ll_getStringValue(addr, casFeatCode_Excerpt);
  }
  /** @generated */    
  public void setExcerpt(int addr, String v) {
        if (featOkTst && casFeat_Excerpt == null)
      jcas.throwFeatMissing("Excerpt", "org.quizreader.textmaker.uima.types.WiktAnnotation");
    ll_cas.ll_setStringValue(addr, casFeatCode_Excerpt, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public WiktAnnotation_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_Excerpt = jcas.getRequiredFeatureDE(casType, "Excerpt", "uima.cas.String", featOkTst);
    casFeatCode_Excerpt  = (null == casFeat_Excerpt) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Excerpt).getCode();

  }
}



    