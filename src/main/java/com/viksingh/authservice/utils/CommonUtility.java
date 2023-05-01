package com.viksingh.authservice.utils;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.util.ObjectUtils;

import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Slf4j
public class CommonUtility {

  private static String decode(String encodedString) {
    return new String(Base64.getUrlDecoder().decode(encodedString));
  }

  @SneakyThrows
  public static JSONObject getPayload(String token) {
    String[] parts = token.split("\\.");
    return new JSONObject(decode(parts[1]));
  }

  @SneakyThrows
  public static String getNameFromToken(String token){
    String name = null;
    JSONObject payload = getPayload(token);
    if(!ObjectUtils.isEmpty(payload)){
      name = payload.getString("name");
    }
    return name;
  }

  @SneakyThrows
  public static String getEmail(String token){
    String name = null;
    JSONObject payload = getPayload(token);
    if(!ObjectUtils.isEmpty(payload)){
      name = payload.getString("email");
    }
    return name;
  }

  @SneakyThrows
  public static String getUserName(String token){
    String userName = null;
    JSONObject payload = getPayload(token);
    if(!ObjectUtils.isEmpty(payload)){
      userName = payload.getString("preferred_username");
    }
    return userName;
  }

  @SneakyThrows
  public static String getExpireTime(String token){
    String exp = null;
    JSONObject payload = getPayload(token);
    if(!ObjectUtils.isEmpty(payload)){
      exp = payload.getString("exp");
    }
    return exp;
  }

  public static boolean isTokenExpired(String token){

    String timestamp = getExpireTime(token);
    long epoch = Long.parseLong(timestamp) *  1000;
    //LocalDateTime dateTime = Instant.ofEpochMilli(epoch)
      //  .atZone(ZoneId.systemDefault()).toLocalDateTime();
    Date expiration = new Date(epoch);
    log.info("Token Expiration Time : {}",expiration);
    return expiration.before(new Date());
  }

  @SneakyThrows
  public static String getTokenExpiration(String token){
    String userName = null;
    JSONObject payload = getPayload(token);
    if(!ObjectUtils.isEmpty(payload)){
      System.out.println(payload);
      userName = payload.getString("preferred_username");
    }
    return userName;
  }

  @SneakyThrows
  public static UUID getUserIdFromToken(String token){
    UUID userId = null;
    JSONObject payload = getPayload(token);
    if(!ObjectUtils.isEmpty(payload)){
      userId = UUID.fromString(payload.getString("sub"));
    }
    return userId;
  }
}
