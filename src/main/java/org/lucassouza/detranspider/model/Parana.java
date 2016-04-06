package org.lucassouza.detranspider.model;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.lucassouza.detranspider.model.vo.Answer;
import org.lucassouza.detranspider.model.vo.Question;
import org.lucassouza.detranspider.view.Displayable;

/**
 *
 * @author Lucas Souza [sorackb@gmail.com]
 */
public class Parana {

  private static final Pattern EXTERNAL_DOUBLE_QUOTES = Pattern.compile("^\"|\"$");
  private final Displayable observer;
  private Boolean stopped;
  private LinkedHashMap<String, String> cookies;
  private Persistence persistence;

  public Parana(Displayable observer) {
    this.persistence = new Persistence();
    this.observer = observer;
    this.stopped = false;
  }

  public void read() {
    Response response;
    byte[] imageContent;

    while (!stopped) {
      try {
        this.cookies = new LinkedHashMap<>();
        response = Jsoup.connect("http://www.simulado.detran.pr.gov.br/detran-prova/jcaptcha").ignoreContentType(true).execute();
        imageContent = response.bodyAsBytes();
        this.cookies.putAll(response.cookies());
        this.observer.displayCaptcha(ImageIO.createImageInputStream(new ByteArrayInputStream(imageContent)));
      } catch (IOException ex) {
        Logger.getLogger(Parana.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
  }

  public void notifyCaptcha(String captcha) {
    Document page;
    Response response;

    captcha = captcha.trim().toUpperCase();

    try {
      response = Jsoup.connect("http://www.simulado.detran.pr.gov.br/detran-prova/simularProva.do?action=iniciarProva")
              .ignoreContentType(true)
              .method(Method.POST)
              .cookies(this.cookies)
              .data("codTipoProva", "3")
              .data("senhaGrafica", captcha)
              .execute();

      page = response.parse();

      if (!page.select("td.msg_aviso").isEmpty()) {
        return;
      }

      response = Jsoup.connect("http://www.simulado.detran.pr.gov.br/detran-prova/simularProva.do?action=calcularResultado&alternativasRespondidas=x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x")
              .ignoreContentType(true)
              .method(Method.POST)
              .cookies(this.cookies)
              .data("numeroQuestao", "")
              .data("alternativaRespondida", "")
              .execute();

      page = response.parse();

      for (Element questionHTML : page.select("div.rounddivred")) {
        List<Answer> answers = new ArrayList<>();
        Question question;

        for (Element image : questionHTML.select("img:not([src*=opcao])")) {
          String newSrc;

          newSrc = this.saveImage("http://www.simulado.detran.pr.gov.br" + image.attr("src"));
          image.attr("src", newSrc);
        }

        question = new Question(StringEscapeUtils.unescapeHtml4(questionHTML.select("div.questao > :not(strong:nth-of-type(1))").html()),
                answers);

        for (Element answerHTML : questionHTML.select("div#respostas > table > tbody > tr > td > span")) {
          Answer answer;
          String answerText;

          if (!answerHTML.attr("style").isEmpty()) {
            answerText = StringEscapeUtils.unescapeHtml4(answerHTML.select("strong").html());
            answerText = EXTERNAL_DOUBLE_QUOTES.matcher(answerText).replaceAll("");
            answer = new Answer(answerText, true);
          } else {
            answerText = StringEscapeUtils.unescapeHtml4(answerHTML.html());
            answerText = EXTERNAL_DOUBLE_QUOTES.matcher(answerText).replaceAll("");
            answer = new Answer(answerText, false);
          }

          answers.add(answer);
        }

        this.persistence.insertQuestion(question);
      }
    } catch (IOException | SQLException ex) {
      Logger.getLogger(Parana.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  private String saveImage(String url) throws IOException {
    String fileName;
    String[] pieces;
    File folder;
    File newFile;
    Response response;
    byte[] imageContent;
    FileOutputStream output;

    pieces = URLDecoder.decode(url, "UTF-8").split("/");
    fileName = pieces[pieces.length - 1];
    response = Jsoup.connect(url).ignoreContentType(true).execute();
    imageContent = response.bodyAsBytes();
    folder = new File("C:/Virtual/imagens");
    folder.mkdirs();
    newFile = new File(folder, fileName);
    newFile.createNewFile();
    output = new FileOutputStream(newFile);
    output.write(imageContent);
    output.close();

    return fileName;
  }

  public void stop() {
    this.stopped = true;
  }
}
