package org.lucassouza.detranspider.model;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import org.lucassouza.detranspider.model.vo.Answer;
import org.lucassouza.detranspider.model.vo.Question;

/**
 *
 * @author Lucas Souza [sorackb@gmail.com]
 */
public class Persistence {

  public void insertQuestion(Question question) throws SQLException {
    Connection connection = SQLServerConnection.openConnection();
    CallableStatement statement;
    String newId;

    statement = connection.prepareCall("{call theoretical.insert_question(?, ?)}");
    statement.registerOutParameter(1, Types.VARCHAR);
    statement.setString(2, question.getEnunciation());
    statement.execute();
    newId = statement.getString(1);

    if (newId != null) {
      for (Answer answer : question.getAnswers()) {
        this.insertAnswer(newId, answer);
      }
    }
  }
  
  private void insertAnswer(String questionId, Answer answer) throws SQLException {
    Connection connection = SQLServerConnection.openConnection();
    CallableStatement statement;

    statement = connection.prepareCall("{call theoretical.insert_answer(?, ?, ?)}");
    statement.setString(1, questionId);
    statement.setString(2, answer.getDescription());
    
    if (answer.isCorrect()) {
      statement.setInt(3, 1);
    } else {
      statement.setInt(3, 0);
    }

    statement.execute();
  }
}
