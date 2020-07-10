package ox;

import static com.google.common.base.Preconditions.checkState;
import static ox.util.Functions.map;
import static ox.util.Utils.getExtension;
import static ox.util.Utils.propagate;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.util.List;

import com.google.common.base.Predicate;

public class File {

  public final java.io.File file;

  private File(String path) {
    this(new java.io.File(path));
  }

  private File(java.io.File file) {
    this.file = file;
  }

  public String getName() {
    return file.getName();
  }

  public String getPath() {
    return file.getPath();
  }

  public boolean exists() {
    return file.exists();
  }

  public File rename(File toName) {
    file.renameTo(toName.file);
    return this;
  }

  public File parent() {
    return new File(file.getParentFile());
  }

  public File child(String name) {
    return new File(new java.io.File(file, name));
  }

  public File sibling(String name) {
    return new File(new java.io.File(file.getParentFile(), name));
  }

  public File withExtension(String extension) {
    String s = getName();
    int i = s.lastIndexOf('.');
    if (i == -1) {
      return new File(file.getPath() + extension);
    }
    return sibling(s.substring(0, i + 1) + extension);
  }

  public String extension() {
    return getExtension(getPath());
  }

  public List<File> children() {
    java.io.File[] files = file.listFiles();
    checkState(files != null, file + " does not exist.");
    return map(files, File::new);
  }

  public File mkdirs() {
    file.mkdirs();
    return this;
  }

  public File delete() {
    if (!file.exists()) {
      return this;
    }
    checkState(file.delete());
    return this;
  }

  public File deleteOnExit() {
    file.deleteOnExit();
    return this;
  }

  public long length() {
    return file.length();
  }

  public InputStream stream() {
    return IO.from(file).asStream();
  }

  @Override
  public String toString() {
    return getPath();
  }

  public void openUI() {
    OS.open(file);
  }

  public void streamLines(Predicate<String> callback) {
    try {
      BufferedReader br = new BufferedReader(new FileReader(file));
      while (true) {
        String line = br.readLine();
        if (line == null || !callback.apply(line)) {
          break;
        }
      }
      IO.close(br);
    } catch (Exception e) {
      throw propagate(e);
    }
  }

  public static File desktop() {
    return new File(OS.getDesktop());
  }

  public static File desktop(String child) {
    return desktop().child(child);
  }

  public static File downloads() {
    return new File(OS.getDownloadsFolder());
  }

  public static File downloads(String child) {
    return downloads().child(child);
  }

  public static File temp(String child) {
    return new File(new java.io.File(OS.getTemporaryFolder(), child));
  }

  public static File of(java.io.File file) {
    return new File(file);
  }

}
