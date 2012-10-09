

/* First created by JCasGen Wed Jun 13 14:33:29 MDT 2012 */
package org.quizreader.textmaker.uima.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Mon Oct 08 20:16:42 MDT 2012
 * XML source: /home/joe/workspace/QuizReaderTextMaker/uima/QuizReaderTypes.xml
 * @generated */
public class WiktAnnotation extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(WiktAnnotation.class);
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated  */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected WiktAnnotation() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public WiktAnnotation(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public WiktAnnotation(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public WiktAnnotation(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** <!-- begin-user-doc -->
    * Write your own initialization here
    * <!-- end-user-doc -->
  @generated modifiable */
  private void readObject() {/*default - does nothing empty block */}
     
 
    
  //*--------------*
  //* Feature: Excerpt

  /** getter for Excerpt - gets 
   * @generated */
  public String getExcerpt() {
    if (WiktAnnotation_Type.featOkTst && ((WiktAnnotation_Type)jcasType).casFeat_Excerpt == null)
      jcasType.jcas.throwFeatMissing("Excerpt", "org.quizreader.textmaker.uima.types.WiktAnnotation");
    return jcasType.ll_cas.ll_getStringValue(addr, ((WiktAnnotation_Type)jcasType).casFeatCode_Excerpt);}
    
  /** setter for Excerpt - sets  
   * @generated */
  public void setExcerpt(String v) {
    if (WiktAnnotation_Type.featOkTst && ((WiktAnnotation_Type)jcasType).casFeat_Excerpt == null)
      jcasType.jcas.throwFeatMissing("Excerpt", "org.quizreader.textmaker.uima.types.WiktAnnotation");
    jcasType.ll_cas.ll_setStringValue(addr, ((WiktAnnotation_Type)jcasType).casFeatCode_Excerpt, v);}    
  }

    