

/* First created by JCasGen Tue Oct 09 21:26:19 MDT 2012 */
package org.quizreader.textmaker.uima.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Sun Oct 14 09:28:34 MDT 2012
 * XML source: /home/joe/workspace/QuizReaderTextMaker/uima/QuizReaderTypes.xml
 * @generated */
public class DefinitionAnnotation extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(DefinitionAnnotation.class);
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
  protected DefinitionAnnotation() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public DefinitionAnnotation(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public DefinitionAnnotation(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public DefinitionAnnotation(JCas jcas, int begin, int end) {
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
    if (DefinitionAnnotation_Type.featOkTst && ((DefinitionAnnotation_Type)jcasType).casFeat_excerpt == null)
      jcasType.jcas.throwFeatMissing("excerpt", "org.quizreader.textmaker.uima.types.DefinitionAnnotation");
    return jcasType.ll_cas.ll_getStringValue(addr, ((DefinitionAnnotation_Type)jcasType).casFeatCode_excerpt);}
    
  /** setter for Excerpt - sets  
   * @generated */
  public void setExcerpt(String v) {
    if (DefinitionAnnotation_Type.featOkTst && ((DefinitionAnnotation_Type)jcasType).casFeat_excerpt == null)
      jcasType.jcas.throwFeatMissing("excerpt", "org.quizreader.textmaker.uima.types.DefinitionAnnotation");
    jcasType.ll_cas.ll_setStringValue(addr, ((DefinitionAnnotation_Type)jcasType).casFeatCode_excerpt, v);}    
  }

    