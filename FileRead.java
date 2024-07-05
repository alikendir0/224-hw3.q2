import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class FileRead {
  private Scanner u;

  public FileRead(String path) {
    try {
      File file = new File(path);
      this.u = new Scanner(file);// scanner initiated
    } catch (FileNotFoundException e) {// if file is not found, error
      e.printStackTrace();
    }
  }

  public String Read() {
    if (u.hasNextLine()) {// if file has lines
      return u.nextLine();// return next line
    } else {// if not return:
      return "No more lines.";
    }
  }
}