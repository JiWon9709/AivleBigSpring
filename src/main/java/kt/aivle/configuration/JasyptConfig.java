package kt.aivle.configuration;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile({"dev","local"})
public class JasyptConfig {

  @Value("${jasypt.encryptor.password:5ReC2cBs}")
  private String key;

  @Bean("jasyptStringEncryptor")
  public StringEncryptor stringEncryptor() {

    PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
    SimpleStringPBEConfig config = new SimpleStringPBEConfig();
    config.setPassword(key);
    config.setAlgorithm("PBEWithMD5AndDES");
    config.setKeyObtentionIterations("1200");
    config.setPoolSize("1");
    config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
    config.setStringOutputType("base64");
    encryptor.setConfig(config);
    return encryptor;
  }

  public static void main(String[] args) {

    String sKey = "5ReC2cBs";
    PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
    SimpleStringPBEConfig config = new SimpleStringPBEConfig();
    System.out.println("==============> " + sKey);
    config.setPassword(sKey);
    config.setAlgorithm("PBEWithMD5AndDES");
    config.setKeyObtentionIterations("1200");
    config.setPoolSize("1");
    config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
    config.setStringOutputType("base64");
    encryptor.setConfig(config);

    String en = encryptor.encrypt("!pmt4321");
    System.out.println("enc : ==> " + en);
    System.out.println(encryptor.decrypt(en));

  }
}
