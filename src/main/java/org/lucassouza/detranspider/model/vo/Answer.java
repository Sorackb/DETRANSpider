package org.lucassouza.detranspider.model.vo;

/**
 *
 * @author Lucas Souza [sorackb@gmail.com]
 */
public class Answer {

  private String id;
  private String description;
  private Boolean correct;

  public Answer(String description, Boolean correct) {
    this.description = description;
    this.correct = correct;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Boolean isCorrect() {
    return correct;
  }

  public void setCorrect(Boolean correct) {
    this.correct = correct;
  }
}
