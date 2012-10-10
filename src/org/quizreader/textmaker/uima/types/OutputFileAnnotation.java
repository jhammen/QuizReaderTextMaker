

/* First created by JCasGen Sun Oct 07 15:01:01 MDT 2012 */
package org.quizreader.textmaker.uima.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Tue Oct 09 21:26:18 MDT 2012
 * XML source: /home/joe/workspace/QuizReaderTextMaker/uima/QuizReaderTypes.xml
 * @generated */
public class OutputFileAnnotation extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(OutputFileAnnotation.class);
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
  protected OutputFileAnnotation() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public OutputFileAnnotation(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public OutputFileAnnotation(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public OutputFileAnnotation(JCas jcas, int begin, int end) {
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
  //* Feature: FilePath

  /** getter for FilePath - gets 
   * @generated */
  public String getFilePath() {
    if (OutputFileAnnotation_Type.featOkTst && ((OutputFileAnnotation_Type)jcasType).casFeat_FilePath == null)
      jcasType.jcas.throwFeatMissing("FilePath", "org.quizreader.textmaker.uima.types.OutputFileAnnotation");
    return jcasType.ll_cas.ll_getStringValue(addr, ((OutputFileAnnotation_Type)jcasType).casFeatCode_FilePath);}
    
  /** setter for FilePath - sets  
   * @generated */
  public void setFilePath(String v) {
    if (OutputFileAnnotation_Type.featOkTst && ((OutputFileAnnotation_Type)jcasType).casFeat_FilePath == null)
      jcasType.jcas.throwFeatMissing("FilePath", "org.quizreader.textmaker.uima.types.OutputFileAnnotation");
    jcasType.ll_cas.ll_setStringValue(addr, ((OutputFileAnnotation_Type)jcasType).casFeatCode_FilePath, v);}    
  }

    