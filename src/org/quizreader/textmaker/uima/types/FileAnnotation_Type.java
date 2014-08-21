
/* First created by JCasGen Sun Oct 14 09:24:01 MDT 2012 */
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
 * Updated by JCasGen Thu Aug 21 11:44:29 MDT 2014
 * @generated */
public class FileAnnotation_Type extends Annotation_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (FileAnnotation_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = FileAnnotation_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new FileAnnotation(addr, FileAnnotation_Type.this);
  			   FileAnnotation_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new FileAnnotation(addr, FileAnnotation_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = FileAnnotation.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("org.quizreader.textmaker.uima.types.FileAnnotation");
 
  /** @generated */
  final Feature casFeat_fileName;
  /** @generated */
  final int     casFeatCode_fileName;
  /** @generated */ 
  public String getFileName(int addr) {
        if (featOkTst && casFeat_fileName == null)
      jcas.throwFeatMissing("fileName", "org.quizreader.textmaker.uima.types.FileAnnotation");
    return ll_cas.ll_getStringValue(addr, casFeatCode_fileName);
  }
  /** @generated */    
  public void setFileName(int addr, String v) {
        if (featOkTst && casFeat_fileName == null)
      jcas.throwFeatMissing("fileName", "org.quizreader.textmaker.uima.types.FileAnnotation");
    ll_cas.ll_setStringValue(addr, casFeatCode_fileName, v);}
    
  
 
  /** @generated */
  final Feature casFeat_output;
  /** @generated */
  final int     casFeatCode_output;
  /** @generated */ 
  public boolean getOutput(int addr) {
        if (featOkTst && casFeat_output == null)
      jcas.throwFeatMissing("output", "org.quizreader.textmaker.uima.types.FileAnnotation");
    return ll_cas.ll_getBooleanValue(addr, casFeatCode_output);
  }
  /** @generated */    
  public void setOutput(int addr, boolean v) {
        if (featOkTst && casFeat_output == null)
      jcas.throwFeatMissing("output", "org.quizreader.textmaker.uima.types.FileAnnotation");
    ll_cas.ll_setBooleanValue(addr, casFeatCode_output, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public FileAnnotation_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_fileName = jcas.getRequiredFeatureDE(casType, "fileName", "uima.cas.String", featOkTst);
    casFeatCode_fileName  = (null == casFeat_fileName) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_fileName).getCode();

 
    casFeat_output = jcas.getRequiredFeatureDE(casType, "output", "uima.cas.Boolean", featOkTst);
    casFeatCode_output  = (null == casFeat_output) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_output).getCode();

  }
}



    