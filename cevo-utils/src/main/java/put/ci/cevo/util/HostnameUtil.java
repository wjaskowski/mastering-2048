package put.ci.cevo.util;

import static org.apache.commons.lang.StringUtils.substringBefore;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

public class HostnameUtil {

	private static final Logger logger = Logger.getLogger(HostnameUtil.class);

	private static final String UNKNOWN = "UNKNOWN";

	public static String getHostName() {
		try {
			InetAddress localHost = InetAddress.getLocalHost();
			return substringBefore(localHost.getHostName(), ".");
		} catch (UnknownHostException e) {
			logger.error("Unable to retrieve localhost information: ", e);
			return UNKNOWN;
		}
	}

	public static String getHostAddress() {
		try {
			InetAddress localHost = InetAddress.getLocalHost();
			return localHost.getHostAddress();
		} catch (UnknownHostException e) {
			logger.error("Unable to retrieve localhost information: ", e);
			return UNKNOWN;
		}
	}
}
