/**
 * (c) Copyright 1998-2021, ANS. All rights reserved.
 */

package fr.gouv.esante.api.sign.ws.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.MessageDigestPasswordEncoder;

/**
 * The Class Secrets.
 */
@SuppressWarnings("deprecation")
public class Secrets {

    /** The Constant LOG_ROUNDS. */
    private static final int LOG_ROUNDS = 12;

    /** The Constant HASH_ALGO. */
    private static String HASH_ALGO = System.getProperty("ws.hashAlgo", "BCRYPT");

    /**
     * The log.
     */
    private static Logger log = LoggerFactory.getLogger(Secrets.class);

    /**
     * Instantiates a new secrets.
     */
    private Secrets() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Returns a hashed password using the provided hash.<br>
     *
     * @param secret the password to be hashed
     * @return the hashed password
     */
	public static String hash(final String secret) {
        if ("SHA256".equals(HASH_ALGO)) {
			final MessageDigestPasswordEncoder passEncoder = new MessageDigestPasswordEncoder(HASH_ALGO);
            return passEncoder.encode(secret);
        }
        return BCrypt.hashpw(secret, BCrypt.gensalt(LOG_ROUNDS));
    }

    /**
     * Returns true if the given password and salt match the hashed value, false
     * otherwise.<br>
     * Note - side effect: the password is destroyed (the char[] is filled with
     * zeros)
     *
     * @param secret the secret to check
     * @param hash   the expected hashed value of the password
     * @return true if the given password match the hashed value, false otherwise
     */
	public static boolean match(final String secret, final String hash) {
        if ("SHA256".equals(HASH_ALGO)) {
            final MessageDigestPasswordEncoder passEncoder = new MessageDigestPasswordEncoder(HASH_ALGO);
            return passEncoder.matches(secret, hash);
        }
        try {
            return BCrypt.checkpw(secret, hash);
        } catch (final IllegalArgumentException i) {    // catch exception when parsing unknown hash format, return false
            log.error(i.getMessage());
        }
        return false;
    }

}
