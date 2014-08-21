

/* First created by JCasGen Sun Oct 14 09:24:01 MDT 2012 */
package org.quizreader.textmaker.uima.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Thu Aug 21 11:44:29 MDT 2014
 * XML source: /home/joe/workspace/QuizReaderTextMaker/uima/qr/QuizReaderTypes.xml
 * @generated */
public class FileAnnotation extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(FileAnnotation.class);
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
  protected FileAnnotation() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public FileAnnotation(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public FileAnnotation(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public FileAnnotation(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** 
   * <!-- begin-user-doc -->
    * Write your own initialization here
    * <!-- end-user-doc -->
  *
   * @generated modifiable 
   */
  private void readObject() {/*default - does nothing empty block */}
     
 
    
  //*--------------*
  //* Feature: fileName

  /** getter for fileName - gets 
   * @generated
   * @return value of the feature 
   */
  public String getFileName() {
    if (FileAnnotation_Type.featOkTst && ((FileAnnotation_Type)jcasType).casFeat_fileName == null)
      jcasType.jcas.throwFeatMissing("fileName", "org.quizreader.textmaker.uima.types.FileAnnotation");
    return jcasType.ll_cas.ll_getStringValue(addr, ((FileAnnotation_Type)jcasType).casFeatCode_fileName);}
    
  /** setter for fileName - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setFileName(String v) {
    if (FileAnnotation_Type.featOkTst && ((FileAnnotation_Type)jcasType).casFeat_fileName == null)
      jcasType.jcas.throwFeatMissing("fileName", "org.quizreader.textmaker.uima.types.FileAnnotation");
    jcasType.ll_cas.ll_setStringValue(addr, ((FileAnnotation_Type)jcasType).casFeatCode_fileName, v);}    
   
    
  //*--------------*
  //* Feature: output

  /** getter for output - gets 
   * @generated
   * @return value of the feature 
   */
  public boolean getOutput() {
    if (FileAnnotation_Type.featOkTst && ((FileAnnotation_Type)jcasType).casFeat_output == null)
      jcasType.jcas.throwFeatMissing("output", "org.quizreader.textmaker.uima.types.FileAnnotation");
    return jcasType.ll_cas.ll_getBooleanValue(addr, ((FileAnnotation_Type)jcasType).casFeatCode_output);}
    
  /** setter for output - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setOutput(boolean v) {
    if (FileAnnotation_Type.featOkTst && ((FileAnnotation_Type)jcasType).casFeat_output == null)
      jcasType.jcas.throwFeatMissing("output", "org.quizreader.textmaker.uima.types.FileAnnotation");
    jcasType.ll_cas.ll_setBooleanValue(addr, ((FileAnnotation_Type)jcasType).casFeatCode_output, v);}    
  }

    