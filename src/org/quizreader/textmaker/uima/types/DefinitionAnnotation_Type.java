
/* First created by JCasGen Tue Oct 09 21:26:19 MDT 2012 */
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
 * Updated by JCasGen Sun Oct 14 09:28:34 MDT 2012
 * @generated */
public class DefinitionAnnotation_Type extends Annotation_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (DefinitionAnnotation_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = DefinitionAnnotation_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new DefinitionAnnotation(addr, DefinitionAnnotation_Type.this);
  			   DefinitionAnnotation_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new DefinitionAnnotation(addr, DefinitionAnnotation_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = DefinitionAnnotation.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("org.quizreader.textmaker.uima.types.DefinitionAnnotation");
 
  /** @generated */
  final Feature casFeat_excerpt;
  /** @generated */
  final int     casFeatCode_excerpt;
  /** @generated */ 
  public String getExcerpt(int addr) {
        if (featOkTst && casFeat_excerpt == null)
      jcas.throwFeatMissing("excerpt", "org.quizreader.textmaker.uima.types.DefinitionAnnotation");
    return ll_cas.ll_getStringValue(addr, casFeatCode_excerpt);
  }
  /** @generated */    
  public void setExcerpt(int addr, String v) {
        if (featOkTst && casFeat_excerpt == null)
      jcas.throwFeatMissing("excerpt", "org.quizreader.textmaker.uima.types.DefinitionAnnotation");
    ll_cas.ll_setStringValue(addr, casFeatCode_excerpt, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public DefinitionAnnotation_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_excerpt = jcas.getRequiredFeatureDE(casType, "excerpt", "uima.cas.String", featOkTst);
    casFeatCode_excerpt  = (null == casFeat_excerpt) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_excerpt).getCode();

  }
}



    