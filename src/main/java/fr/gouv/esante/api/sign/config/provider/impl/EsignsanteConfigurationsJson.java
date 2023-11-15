/**
 * (c) Copyright 1998-2023, ANS. All rights reserved.
 */

package fr.gouv.esante.api.sign.config.provider.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidParameterException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import jakarta.annotation.PostConstruct;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.gouv.esante.api.sign.config.provider.IEsignsanteConfigurationsProvider;
import fr.gouv.esante.api.sign.ws.bean.config.IGlobalConf;
import fr.gouv.esante.api.sign.ws.bean.config.impl.GlobalConfJson;
import fr.gouv.esante.api.sign.ws.bean.object.CaConf;
import fr.gouv.esante.api.sign.ws.bean.object.CertVerifConf;
import fr.gouv.esante.api.sign.ws.bean.object.ProofConf;
import fr.gouv.esante.api.sign.ws.bean.object.SignVerifConf;
import fr.gouv.esante.api.sign.ws.bean.object.SignatureConf;

/**
 * The Class ESignSanteSanteConfigurationsJson.
 */
@Component
public class EsignsanteConfigurationsJson implements IEsignsanteConfigurationsProvider {

    /** The log. */
    private static final Logger log = LoggerFactory.getLogger(EsignsanteConfigurationsJson.class);

    /**
     * SLEEP.
     */
    private static final long SLEEP = 60000;

    /**
     * whether or not we're on app startup.
     */
    private boolean startup = true;

    /**
     * load global conf from file path.
     *
     * @return IGlobalConf
     */
    @Override
    public IGlobalConf load() {
        final File jsonFile = new File(System.getProperty("ws.conf"));
        return loadConf(jsonFile);
    }

    /**
     * load global conf from file.
     *
     * @param jsonFile json file
     * @return IGlobalConf
     */
    private IGlobalConf loadConf(final File jsonFile) {
        IGlobalConf conf = null;
        final ObjectMapper mapper = new ObjectMapper();
        boolean validConf = true;

        try {
            final String jsonConf = new String(Files.readAllBytes(Paths.get(jsonFile.toURI())));
            conf = mapper.readValue(jsonConf, GlobalConfJson.class);
            validConf = conf != null;
            if (validConf) {
                for (final SignatureConf signConf : conf.getSignature()){
                    validConf &= signConf.checkValid(); // Check if signConf is valid
                }
                for (final ProofConf proofConf : conf.getProof()){
                    validConf &= proofConf.checkValid(); // Check if proofConf is valid
                }
                for (final SignVerifConf signVerifConf : conf.getSignatureVerification()){
                    validConf &= signVerifConf.checkValid(); // Check if signVerifConf is valid
                }
                for (final CertVerifConf certVerifConf : conf.getCertificateVerification()){
                    validConf &= certVerifConf.checkValid(); // Check if certVerifConf is valid
                }
                for (final CaConf caConf : conf.getCa()){
                    validConf &= caConf.checkValid(); // Check if caConf is valid
                }
            }
        } catch (final IllegalAccessException | IOException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }

        if (validConf) {
            log.info("Configurations loaded.");
        } else {
            log.error("Could not load configurations.");
            if (startup) {
                throw new InvalidParameterException("Error in configuration file");
            } else {
                conf = null;
            }
        }
        return conf;
    }

    /**
     * Inner Class ReloadConfigurationsJson.
     */
    @Component
    public class ReloadConfigurationsJson extends Thread {

        /** The log. */
        private final Logger log = LoggerFactory.getLogger(ReloadConfigurationsJson.class);

        /**
         * globalConf.
         */
        @Autowired
        private IGlobalConf globalConf;

        /**
         * stop.
         */
        private final AtomicBoolean stop = new AtomicBoolean(false);

        /**
         * checksum.
         */
        private String checksum;

        /**
         * Reload configuration file on change.
         */
        @PostConstruct
        public void reloadConfiguration() {
            final File file = new File(System.getProperty("ws.conf"));
            try {
                checksum = getFileChecksum(MessageDigest.getInstance("SHA-512"), file);
            } catch (IOException | NoSuchAlgorithmException e) {
            	log.error(Arrays.toString(e.getStackTrace()));
            }
            start();
        }

        private void reloadConf(final File jsonFile) {
            startup = false;
            final IGlobalConf conf = loadConf(jsonFile);
            if (conf != null) {
                globalConf.setCa(conf.getCa());
                globalConf.setCertificateVerification(conf.getCertificateVerification());
                globalConf.setProof(conf.getProof());
                globalConf.setSignature(conf.getSignature());
                globalConf.setSignatureVerification(conf.getSignatureVerification());
                log.info("New configurations loaded.");
            } else {
                log.error("Could not load new configurations, will continue using current valid configurations.");
            }

        }

        /**
         * is thread stopped.
         *
         * @return boolean boolean
         */
        public boolean isStopped() {
            return stop.get();
        }

        /**
         * stop thread.
         */
        public void stopThread() {
            stop.set(true);
        }

        /**
         * reload global conf on change in file.
         *
         * @param file file
         */
        private void doOnChange(final File file) {
            reloadConf(file);
        }

        /**
         * run thread, once started it will run indefinitely in parallel.
         */
        @Override
        public void run() {
            log.info("Configuration file poll thread started.");

            MessageDigest digest;
            try {
            	digest = MessageDigest.getInstance("SHA-512");
                while (!isStopped()) { // infinite loop basically
                    //Get the newChecksum
                    final File file = new File(System.getProperty("ws.conf"));
                    String newChecksum = getFileChecksum(digest, file);
                    if (newChecksum.equals(checksum)) {
                        log.debug("pas de nouvelle configuration détecté");
                    } else {
                        log.debug("une nouvelle configuration a été détecté");
                        checksum = newChecksum;
                        // do something only if we detect a modification to the specific file
                        doOnChange(file);
                    }
                    Thread.sleep(SLEEP);
                }
            } catch (NoSuchAlgorithmException | IOException e) {
                log.error("une erreur est survenue durant la surveillance du fichier de configuration : ", e);
            } catch (InterruptedException e) {
            	log.error("une erreur est survenue durant la surveillance du fichier de configuration : ", e);
            	Thread.currentThread().interrupt();
            }
        }

        /**
         * returns file checksum.
         *
         * @param file file
         * @param digest digest
         * @throws IOException 
         */
        private String getFileChecksum(MessageDigest digest, File file) throws IOException
        {
            //Get file input stream for reading the file content
			try (FileInputStream fis = new FileInputStream(file)){
				//Create byte array to read data in chunks
	            byte[] byteArray = new byte[1024];
	            int bytesCount;
	            //Read file data and update in message digest
	            while ((bytesCount = fis.read(byteArray)) != -1) {
	                digest.update(byteArray, 0, bytesCount);
	            }
			} catch (IOException e) {
				log.error("stream creation failed while getting the file checksum");
				throw new IOException(e);
			}
            
            //Get the hash's bytes
            byte[] bytes = digest.digest();
            //This bytes[] has bytes in decimal format; Convert it to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            //return complete hash
            return sb.toString();
        }

    }

}
