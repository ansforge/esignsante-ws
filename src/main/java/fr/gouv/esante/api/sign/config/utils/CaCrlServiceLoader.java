/**
 * (c) Copyright 1998-2023, ANS. All rights reserved.
 */

package fr.gouv.esante.api.sign.config.utils;

import fr.gouv.esante.api.sign.service.ICACRLService;
import fr.gouv.esante.api.sign.ws.bean.object.CaConf;

import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Class CaCrlServiceLoader.
 */
public class CaCrlServiceLoader {

	/**
	 * logger.
	 */
	private static Logger log = LoggerFactory.getLogger(CaCrlServiceLoader.class);

	/**
	 * private constructor to hide implicit public one.
	 */
	private CaCrlServiceLoader() {
	}

	/**
	 * Load ca crl icacrl service.
	 *
	 * @param serviceCaCrl the service ca crl
	 * @param listCaConf   the list ca conf
	 * @return the icacrl service
	 * @throws IOException the io exception
	 */
	public static ICACRLService loadCaCrl(final ICACRLService serviceCaCrl, final List<CaConf> listCaConf)
			throws IOException {
		final List<String> certList = listCaConf.stream().map(CaConf::getCertificate).collect(Collectors.toList());
		loadCa(serviceCaCrl, certList);

		final List<String> crlList = listCaConf.stream().map(CaConf::getCrl).collect(Collectors.toList());
		loadCrl(serviceCaCrl, crlList);
		return serviceCaCrl;
	}

	private static void loadCrl(final ICACRLService serviceCaCrl, final List<String> crlList) throws IOException {
		final File crlFile;
		if (SystemUtils.IS_OS_UNIX) {
			FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions
					.asFileAttribute(PosixFilePermissions.fromString("rwx------"));
			crlFile = Files.createTempFile("ca-bundle", ".crl", attr).toFile();
		} else {
			crlFile = Files.createTempFile("ca-bundle", ".crl").toFile();
			boolean readableSet = crlFile.setReadable(true, true);
			boolean writableSet = crlFile.setWritable(true, true);
			boolean executableSet = crlFile.setExecutable(true, true);
			log.debug("readable set: " + readableSet + "; writable set: " + writableSet + "; executable set: "
					+ executableSet);
		}

		final CRLLoader crlLoader = new CRLLoader(crlList);
		try {
			crlLoader.buildCRLBundle(crlFile);
		} catch (final GeneralSecurityException e) {
			log.error(ExceptionUtils.getStackTrace(e));
		}
		log.info("Chargement du bundle des CRL, chemin : {}", crlFile.getAbsolutePath());
		serviceCaCrl.loadCRL(crlFile); // On télécharge les CRLs puis on contruit un bundle qui contient toutes les
										// CRLs
		if (!crlFile.delete()) {
			log.error("Le fichier {} n'a pas pu être supprimé", crlFile.getAbsolutePath());
		}
	}

	private static void loadCa(final ICACRLService serviceCaCrl, final List<String> certList) throws IOException {
		final File caFile;
		if (SystemUtils.IS_OS_UNIX) {
			FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions
					.asFileAttribute(PosixFilePermissions.fromString("rwx------"));
			caFile = Files.createTempFile("ca-bundle", ".crt", attr).toFile();
		} else {
			caFile = Files.createTempFile("ca-bundle", ".crt").toFile();
			boolean readableSet = caFile.setReadable(true, true);
			boolean writableSet = caFile.setWritable(true, true);
			boolean executableSet = caFile.setExecutable(true, true);
			log.debug("readable set: " + readableSet + "; writable set: " + writableSet + "; executable set: "
					+ executableSet);
		}
		try (final BufferedWriter writer = new BufferedWriter(new FileWriter(caFile))) {
			for (final String cert : certList) {
				writer.write(cert); // On contruit un bundle qui contient tout les CAs
				writer.newLine();
			}
			log.info("Chargement du bundle des AC, chemin : {}", caFile.getAbsolutePath());
		} catch (final IOException e) {
			log.error(ExceptionUtils.getStackTrace(e));
		}
		serviceCaCrl.loadCA(caFile);
		if (!caFile.delete()) {
			log.error("Le fichier {} n'a pas pu être supprimé", caFile.getAbsolutePath());
		}
	}

}
