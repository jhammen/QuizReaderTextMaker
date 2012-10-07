
/* First created by JCasGen Sun Oct 07 15:01:01 MDT 2012 */
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
 * Updated by JCasGen Sun Oct 07 15:04:38 MDT 2012
 * @generated */
public class OutputFileAnnotation_Type extends Annotation_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (OutputFileAnnotation_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = OutputFileAnnotation_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new OutputFileAnnotation(addr, OutputFileAnnotation_Type.this);
  			   OutputFileAnnotation_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new OutputFileAnnotation(addr, OutputFileAnnotation_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = OutputFileAnnotation.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("org.quizreader.textmaker.uima.types.OutputFileAnnotation");



  /** @generated */
  final Feature casFeat_FilePath;
  /** @generated */
  final int     casFeatCode_FilePath;
  /** @generated */ 
  public String getFilePath(int addr) {
        if (featOkTst && casFeat_FilePath == null)
      jcas.throwFeatMissing("FilePath", "org.quizreader.textmaker.uima.types.OutputFileAnnotation");
    return ll_cas.ll_getStringValue(addr, casFeatCode_FilePath);
  }
  /** @generated */    
  public void setFilePath(int addr, String v) {
        if (featOkTst && casFeat_FilePath == null)
      jcas.throwFeatMissing("FilePath", "org.quizreader.textmaker.uima.types.OutputFileAnnotation");
    ll_cas.ll_setStringValue(addr, casFeatCode_FilePath, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public OutputFileAnnotation_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_FilePath = jcas.getRequiredFeatureDE(casType, "FilePath", "uima.cas.String", featOkTst);
    casFeatCode_FilePath  = (null == casFeat_FilePath) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_FilePath).getCode();

  }
}



    