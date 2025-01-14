package kt.aivle;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import kt.aivle.util.Aes256Util;
import kt.aivle.util.Sha256Util;
import org.junit.jupiter.api.Test;

public class jihoonTest {

  @Test
  public void test() {
    try {
      System.out.println(Sha256Util.encrypt("admin!"));
    } catch (NoSuchAlgorithmException e) {
      System.err.println("SHA-256 algorithm not available: " + e.getMessage());
      // 예외 처리 코드 추가
    }
  }

  @Test
  public static void main(String[] args) throws GeneralSecurityException, UnsupportedEncodingException {
    System.out.println(Aes256Util.encrypt("0000000000"));
    System.out.println(Aes256Util.decrypt(Aes256Util.encrypt("0000000000")));
  }
}
