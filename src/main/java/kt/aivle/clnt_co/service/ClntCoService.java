package kt.aivle.clnt_co.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kt.aivle.base.BaseMsg;
import kt.aivle.base.BaseResListModel;
import kt.aivle.base.BaseResModel;
import kt.aivle.clnt_co.mapper.ClntCoMapper;
import kt.aivle.clnt_co.model.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ClntCoService {
  private static final Logger log = LogManager.getLogger(ClntCoService.class);
  @Autowired
  private ClntCoMapper clntCoMapper;

  @Value("${file.path}")
  private String dir;


  /**
   * 테스트 삽입
   *
   * @param testRequest
   * @return
   */
  @Transactional
  public BaseResModel regTest(TestRequest testRequest) {
    BaseResModel result = new BaseResModel<>();

    try {
      int cnt = clntCoMapper.regTest(testRequest);
      if (cnt == 0) {
        result.setResultMsg(BaseMsg.FAILED.getValue());
        result.setResultCode(BaseMsg.FAILED.getCode());
      }
    } catch (Exception e) {
      throw new RuntimeException("테스트 등록 중 에러가 발생했습니다." + e);
    }
    return result;
  }

  /**
   * 테스트 삽입
   *
   * @param testRequest2
   * @return
   */
  @Transactional
  public HospitalInfoListResponse emergencyResult(TestRequest2 testRequest2) {
    HospitalInfoListResponse result = new HospitalInfoListResponse();

    RestTemplate restTemplate = new RestTemplate();

    // Build the URL with query parameters
    String baseUrl = "http://127.0.0.1:8000/hospital_by_module";

    String text = testRequest2.getText();
    String lng = testRequest2.getLng();
    String lat = testRequest2.getLat();

    // Construct the URL with parameters
    String url = String.format("%s?text=%s&lng=%s&lat=%s",
        baseUrl, text, lng, lat);

    // Send the GET request
    ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);

    // Parse the response body
    ObjectMapper mapper = new ObjectMapper();

    try {
      JsonNode root = mapper.readTree(response.getBody());
      JsonNode resultArray = root.path("result");
      List<HospitalInfo> list = new ArrayList<HospitalInfo>();
      for (JsonNode hospitalNode : resultArray) {
        HospitalInfo data = new HospitalInfo();
        data.setName(hospitalNode.path("병원이름").asText());
        data.setAddress(hospitalNode.path("주소").asText());
        data.setEmergencyType(hospitalNode.path("응급의료기관 종류").asText());
        data.setPhone1(hospitalNode.path("전화번호 1").asText());
        data.setPhone3(hospitalNode.path("전화번호 3").asText());
        data.setLatitude(hospitalNode.path("위도").asText());
        data.setLongitude(hospitalNode.path("경도").asText());
        data.setDistance(hospitalNode.path("거리").asText());
        list.add(data);
      }
      result.setInfoList(list);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }

    return result;
  }

  /**
   * 테스트 리스트
   *
   * @param testRequest
   * @return
   */
  @Transactional
  public BaseResListModel<TestList> testList(TestRequest testRequest) {
    BaseResListModel<TestList> result = new BaseResListModel<>();

    try {
      List<TestList> list = clntCoMapper.testList(testRequest);
      if (list.size() == 0) {
        result.setResultMsg(BaseMsg.FAILED.getValue());
        result.setResultCode(BaseMsg.FAILED.getCode());
      } else {
        result.setData(list);
      }
    } catch (Exception e) {
      throw new RuntimeException("리스트를 불러오던 중 에러가 발생했습니다." + e);
    }
    return result;
  }
}
