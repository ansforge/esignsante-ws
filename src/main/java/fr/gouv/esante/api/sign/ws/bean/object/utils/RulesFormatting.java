/**
 * (c) Copyright 1998-2023, ANS. All rights reserved.
 */

package fr.gouv.esante.api.sign.ws.bean.object.utils;

import fr.gouv.esante.api.sign.ws.bean.object.SignVerifConf;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * The type Rules formatting.
 */
public class RulesFormatting {

    /** The log. */
    private static final Logger log = LoggerFactory.getLogger(SignVerifConf.class);

    /** The rules definition. */
    private static Properties rulesDefinition = new Properties();

    /**
     * Instantiates a new Rules formatting.
     */
    private RulesFormatting() {
    }

    /**
     * Load mapper between rule and its description.
     */
    static {
        try (final InputStream is =
                     Thread.currentThread().getContextClassLoader().getResourceAsStream("rules.properties")) {

            if (is != null) {
                rulesDefinition.load(is);
            } else {
                rulesDefinition.put("defaultKey", "defaultValue");
            }
        } catch (final IOException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * Format rules.
     *
     * @param rules the rules
     * @return the string
     */
    public static String formatCertRules(final String rules) {
        // remove duplicate rules from list
        final ArrayList<String> rulesList = new ArrayList<>(Arrays.asList(rules.trim().split(",")));
        return formatRules(rulesList);
    }

    /**
     * Reshaped rules.
     *
     * @param rules the rules
     * @return the string
     */
    public static String formatSignRules(final String rules) {
        // split string into list
        final ArrayList<String> rulesList = new ArrayList<>(Arrays.asList(rules.trim().split(",")));

        try (final InputStream is =
                     Thread.currentThread().getContextClassLoader().getResourceAsStream("obligatory-rules.properties")) {
            assert is != null;
            Scanner obligatoryRules = new Scanner(is);
            while (obligatoryRules.hasNextLine()){
                String rule = obligatoryRules.nextLine();
                if (!rule.startsWith("#")) {
                    rulesList.add(rule); // add obligatory rules
                }
            }
            obligatoryRules.close();
        } catch (final IOException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return formatRules(rulesList);
    }

    /**
     * Reshaped rules.
     *
     * @param rulesList the rules list
     * @return the string
     */
    private static String formatRules(final ArrayList<String> rulesList) {
        // linked hashset to remove duplicates
        final LinkedHashSet<String> lhSetRules = new LinkedHashSet<>(rulesList);
        // construct array from rulesDefinition keys
        final List<String> keysList = new ArrayList<>();
        final Enumeration<Object> keys = rulesDefinition.keys();
        while (keys.hasMoreElements()) {
            keysList.add((String) keys.nextElement());
        }

        final HashSet<String> madeUpRules = new HashSet<>(lhSetRules);
        madeUpRules.removeAll(keysList);   // get extra rules from loaded map
        lhSetRules.removeAll(madeUpRules); // remove extra rules

        // create array from the LinkedHashSet and replace rules with correct value
        return Arrays.toString(lhSetRules.toArray(new String[0]))
                .replaceAll("\\[", "").replaceAll("]", "").replaceAll("\\s+", "");
    }

}
