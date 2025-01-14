package kt.aivle.util;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Service;

/**
 * 양방향 암호화 알고리즘인 AES256 암호화를 지원하는 클래스
 */
@Service
public class Aes256Util {

  private static String iv;
  private static SecretKeySpec keySpec;

  /**
   * 16자리의 키값을 입력하여 객체를 생성하고자 하는 경우 수정 필요
   */
  final static String key = "nexlink012345678";//9012345678901234";

  public Aes256Util() {
    super();
  }

  private static void init() throws UnsupportedEncodingException {
    iv = key.substring(0, 16);
    byte[] keyBytes = new byte[16];
    byte[] b = key.getBytes("UTF-8");
    int len = b.length;
    if (len > keyBytes.length) {
      len = keyBytes.length;
    }
    System.arraycopy(b, 0, keyBytes, 0, len);
    keySpec = new SecretKeySpec(keyBytes, "AES");
  }

  /**
   * AES256 으로 암호화
   *
   * @param str 암호화할 문자열
   * @return
   * @throws NoSuchAlgorithmException
   * @throws GeneralSecurityException
   * @throws UnsupportedEncodingException
   */
  public static String encrypt(String str)
      throws NoSuchAlgorithmException, GeneralSecurityException, UnsupportedEncodingException {
    init();
    Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
    c.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes()));
    byte[] encrypted = c.doFinal(str.getBytes("UTF-8"));
    String enStr = new String(Base64.encodeBase64(encrypted));
    return enStr;
  }

  /**
   * AES256으로 암호화된 txt를 복호화
   *
   * @param str 복호화할 문자열
   * @return
   * @throws NoSuchAlgorithmException
   * @throws GeneralSecurityException
   * @throws UnsupportedEncodingException
   */
  public static String decrypt(String str)
      throws NoSuchAlgorithmException, GeneralSecurityException, UnsupportedEncodingException {
    init();
    Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
    c.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes()));
    byte[] byteStr = Base64.decodeBase64(str.getBytes());
    return new String(c.doFinal(byteStr), "UTF-8");
  }

}
