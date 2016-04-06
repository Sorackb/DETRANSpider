package org.lucassouza.detranspider.view;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import org.lucassouza.detranspider.model.Parana;

/**
 *
 * @author Lucas Souza [sorackb@gmail.com]
 */
public class MainScreen implements Displayable {

  private final Parana parana;

  public MainScreen() {
    this.parana = new Parana(this);
    this.parana.read();
  }

  @Override
  public void displayCaptcha(ImageInputStream image) {
    String captcha;
    BufferedImage img;
    ImageIcon icon;
    JLabel label;

    try {
      img = ImageIO.read(image);

      icon = new ImageIcon(img);
      label = new JLabel(icon);
      captcha = JOptionPane.showInputDialog(null, label);

      if (captcha == null) {
        this.parana.stop();
      } else {
        this.parana.notifyCaptcha(captcha);
      }
    } catch (IOException ex) {
      Logger.getLogger(MainScreen.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  public static void main(String args[]) {
    MainScreen mainScreen = new MainScreen();
  }
}
