package org.lucassouza.detranspider.model.vo;

import java.util.List;

/**
 *
 * @author Lucas Souza [sorackb@gmail.com]
 */
public class Question {

  private String enunciation;
  private List<Answer> answers;

  public Question(String enunciation, List<Answer> answers) {
    this.enunciation = enunciation;
    this.answers = answers;
  }

  public String getEnunciation() {
    return enunciation;
  }

  public void setEnunciation(String enunciation) {
    this.enunciation = enunciation;
  }

  public List<Answer> getAnswers() {
    return answers;
  }

  public void setAnswers(List<Answer> answers) {
    this.answers = answers;
  }
}
