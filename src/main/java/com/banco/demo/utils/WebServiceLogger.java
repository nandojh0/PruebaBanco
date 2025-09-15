package com.banco.demo.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.security.SecureRandom;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author oscar.morales
 */
@Component
public class WebServiceLogger {

    @Value("${SPRING.ENCRPYTION.SECRET-KEY}")
    private String encryptionSecretKey;

    private static final String ENVIRONMENT = System.getProperty("os.name").toLowerCase();

    private static final int TAG_LENGTH_BITS = 128;

    public void logLine(final String logName, final String message) {
        try {
            final String logFullName = getCurrentDate() + "_" + logName + ".txt";
            String encryptedMessage = encryptMessage(getLogsFolderPath(), logFullName, message).orElse(message);
            final String logFullMessage = getCurrentTime() + " " + encryptedMessage;
            writeInFile(getLogsFolderPath(), logFullName, logFullMessage);
        } catch (Exception ex) {
            Logger.getLogger(WebServiceLogger.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String getLogsFolderPath() {
        String logsFolderPath;
        if (ENVIRONMENT.contains("nix")
                || ENVIRONMENT.contains("nux")
                || ENVIRONMENT.contains("aix")) {
            logsFolderPath = System.getProperty("catalina.base") + File.separator + "webapps"
                    + File.separator + "log_WSPrueba";
        } else {
            logsFolderPath = System.getProperty("user.dir") + File.separator + "webapps"
                    + File.separator + "log_WSPrueba";
        }
        return logsFolderPath;
    }

    private Optional<String> encryptMessage(final String filePath,
            final String logName,
            String message) {

        Optional<String> encryptedMessage = Optional.empty();

        try {
            final List<String> fieldsToEncrypt = findFieldsToEncrypt(message);

            if (!fieldsToEncrypt.isEmpty()) {
                final SecureRandom random = new SecureRandom();
                final byte[] bytesIV = new byte[12];
                random.nextBytes(bytesIV);
                final Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
                final String initVector = encoder.encodeToString(bytesIV);
                final String secretKey = encryptionSecretKey;
                /*
                 * Documentacion de seguridad: El IV se genera aleatoriamente de
                 * forma segura. Se codifica en base 64 para que pueda ser
                 * representado como caracteres imprimibles tal que pueda ser
                 * almacenado en los logs. Esto no le resta aleatoriedad o
                 * seguridad a su procedimiento de generacion.
                 */
                final SecretKeySpec sks = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "AES");
                writeInFile(filePath, logName, "IV: " + initVector);
                for (String field : fieldsToEncrypt) {
                    Optional<String> encryptedField = encryptField(field, initVector.getBytes(), sks);
                    message = encryptedField.isPresent()
                            ? message.replace("<*" + field + "*>", "<*" + encryptedField.get() + "*>")
                            : message;
                }
                encryptedMessage = Optional.of(message);
            }
        } catch (Exception ex) {
            Logger.getLogger(WebServiceLogger.class.getName()).log(Level.SEVERE, null, ex);
            encryptedMessage = Optional.empty();
        }
        return encryptedMessage;
    }

    private List<String> findFieldsToEncrypt(final String text) {

        List<String> fieldsToEncrypt = new ArrayList<>();
        for (String s : text.split(Pattern.quote("<*"))) {
            if (s.contains("*>")) {
                fieldsToEncrypt.add(s.split(Pattern.quote("*>"))[0]);
            }
        }
        return fieldsToEncrypt;
    }

    private Optional<String> encryptField(final String field,
            final byte[] iv,
            final SecretKeySpec sks) {

        Optional<String> encryptedField = Optional.empty();
        try {

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(TAG_LENGTH_BITS, iv);
            cipher.init(Cipher.ENCRYPT_MODE, sks, gcmParameterSpec);
            byte[] encrypted = cipher.doFinal(field.getBytes());
            encryptedField = Optional.of(java.util.Base64.getEncoder().encodeToString(encrypted));
        } catch (Exception ex) {
            Logger.getLogger(WebServiceLogger.class.getName()).log(Level.SEVERE, null, ex);
        }
        return encryptedField;
    }

    private void checkRootFileExists(final String filePath) {

        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.mkdirs();
                setPermissions(filePath);
            }
        } catch (Exception ex) {
            Logger.getLogger(WebServiceLogger.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void setPermissions(final String filePath) {

        try {
            if (ENVIRONMENT.contains("nix")
                    || ENVIRONMENT.contains("nux")
                    || ENVIRONMENT.contains("aix")) {
                File fileLog = new File(filePath);
                Set<PosixFilePermission> perms = new HashSet<>();
                perms.add(PosixFilePermission.OWNER_READ);
                perms.add(PosixFilePermission.OWNER_WRITE);
                perms.add(PosixFilePermission.OWNER_EXECUTE);
                perms.add(PosixFilePermission.GROUP_READ);
                perms.add(PosixFilePermission.GROUP_EXECUTE);
                /*
                 * Documentacion Seguridad: La linea de abajo es necesaria para
                 * que Plutón pueda acceder a los logs del desarrollo en el
                 * ambiente de producción.
                 */
                perms.add(PosixFilePermission.OTHERS_READ);
                Files.setPosixFilePermissions(fileLog.toPath(), perms);
            }
        } catch (Exception ex) {
            Logger.getLogger(WebServiceLogger.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void writeInFile(final String filePath, final String fileName, final String text) {
        checkRootFileExists(filePath);
        try (FileWriter fileWriter = new FileWriter(filePath + File.separator + fileName, true); BufferedWriter bufferWriter = new BufferedWriter(fileWriter)) {
            bufferWriter.write(text);
            bufferWriter.newLine();
            setPermissions(filePath + File.separator + fileName);
        } catch (Exception ex) {
            Logger.getLogger(WebServiceLogger.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String getCurrentDate() {
        final Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(date);
    }

    private String getCurrentTime() {
        final Time currentTime = new Time(new Date().getTime());
        return currentTime.toString();
    }

}
