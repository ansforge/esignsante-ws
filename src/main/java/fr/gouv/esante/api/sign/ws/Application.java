/**
 * (c) Copyright 1998-2021, ANS. All rights reserved.
 */

package fr.gouv.esante.api.sign.ws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.util.Arrays;
import java.util.Optional;

@SpringBootApplication
@ComponentScan(basePackages = {"fr.gouv.esante.api.sign.ws", "fr.gouv.esante.api.sign.ws.api" , "fr.gouv.esante.api.sign.config"})
public class Application implements CommandLineRunner {

    /** The log. */
    private static final Logger log = LoggerFactory.getLogger(Application.class);

    @Override
    public void run(final String... arg0) throws Exception {
        if (arg0.length > 0 && arg0[0].equals("exitcode")) {
            throw new ExitException();
        }
    }

    public static void main(final String[] args) throws Exception {
        if (!"true".equals(System.getProperty("com.sun.org.apache.xml.internal.security.ignoreLineBreaks"))) {
            log.warn("Lines in signed XML document will end with the ASCII character for carriage return '&#13;'. " +
                    "You can workaround this issue by setting the " +
                    "com.sun.org.apache.xml.internal.security.ignoreLineBreaks system property to true");
        }
        log.debug("Args : {}", Arrays.toString(args));
        final Optional<String> confPath = Arrays.stream(args).filter(s -> s.startsWith("--ws.conf=")).findAny();
        if (!confPath.isPresent()) {
            throw new IllegalArgumentException("Missing program argument : '--ws.conf='");
        }
        System.setProperty("ws.conf", confPath.get().substring(confPath.get().lastIndexOf("=") + 1));

        final Optional<String> hashAlgo = Arrays.stream(args).filter(s -> s.startsWith("--ws.hashAlgo=")).findAny();
        hashAlgo.ifPresent(s -> System.setProperty("ws.hashAlgo", s.substring(s.lastIndexOf("=") + 1)));
        new SpringApplication(Application.class).run(args);
    }

    static class ExitException extends RuntimeException implements ExitCodeGenerator {
        private static final long serialVersionUID = 1L;

        @Override
        public int getExitCode() {
            return 10;
        }

    }

}
