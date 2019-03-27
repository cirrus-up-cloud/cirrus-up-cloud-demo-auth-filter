package cloud.cirrusup.filter;

import com.google.common.base.Splitter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Formatter;
import java.util.List;

/**
 * Authentication filter.
 * See https://tools.ietf.org/html/rfc2617
 */
@Configuration
public class AuthenticationFilter implements Filter {

  /**
   * Class logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(AuthenticationFilter.class);

  private static final String API_KEY = "Authorization";

  private static final Base64.Decoder DECODER = Base64.getDecoder();

  @Value("${filters.authorization.api.key}")
  private String apiKey;

  /**
   * {@inheritDoc}
   */
  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

    if (!isValidRequestHeader(req) || !checkHeaderValueIsValid(req)) {

      res.resetBuffer();
      res.getOutputStream().write("Authentication issue: Authorization header is missing or invalid".getBytes());
      ((HttpServletResponse) res).setStatus(401);

    } else {

      chain.doFilter(req, res);
    }
  }

  private boolean checkHeaderValueIsValid(ServletRequest req) {

    String value = ((HttpServletRequest) req).getHeader(API_KEY);

    if (!value.toLowerCase().startsWith("basic")) {
      return false;
    }

    List<String> suffix = Splitter.on(" ").splitToList(value);
    if (suffix.size() != 2) {
      return false;
    }

    String plainValue;
    try {

      plainValue = new String(DECODER.decode(suffix.get(1)));

    } catch (IllegalArgumentException e) {

      LOG.info("Exception on decoding the Base64 value: " + e);
      return false;
    }

    try {

      MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
      return byteToHex(messageDigest.digest(plainValue.getBytes())).equals(apiKey);

    } catch (Exception e) {

      LOG.info("Exception on computing message digest for authentication." + e);
    }

    return false;
  }

  private String byteToHex(final byte[] hash) {

    Formatter formatter = new Formatter();

    for (byte b : hash) {
      formatter.format("%02x", b);
    }

    String result = formatter.toString();
    formatter.close();
    return result;
  }


  private boolean isValidRequestHeader(ServletRequest req) {

    return req != null && req instanceof HttpServletRequest
      && StringUtils.isNotEmpty(((HttpServletRequest) req).getHeader(API_KEY));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void destroy() {
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void init(FilterConfig arg0) throws ServletException {
  }
}
