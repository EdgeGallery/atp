/*
 * Copyright 2021 Huawei Technologies Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.edgegallery.atp.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.encoders.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SignatureValidation {
    private static final Logger LOGGER = LoggerFactory.getLogger(SignatureValidation.class);

    private static String SIGNATURE_VERIFY_FAILED = "signature verify failed.";

    private static String INNER_ERROR = "inner error.";

    private static String SUCCESS = "success";

    static {
        try {
            Security.addProvider(new BouncyCastleProvider());
        } catch (Exception e) {
            LOGGER.error("add provider failed. {}", e);
        }
    }

    /**
     * execute test case.
     *
     * @param filePath csar file path
     * @param context context info
     * @return execute result
     */
    public static String verify(String filePath, Map<String, String> context) {
        try (ZipFile zipFile = new ZipFile(filePath)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                // root directory and file is end of mf
                if (entry.getName().split("/").length == 1 && entry.getName().endsWith("mf")) {
                    return validateSignature(zipFile, entry) ? SUCCESS : SIGNATURE_VERIFY_FAILED;
                }
            }
        } catch (CMSException e) {
            LOGGER.error("CMS exception, {}", e);
        } catch (IOException e) {
            LOGGER.error("IO exception, {}", e);
        }

        return INNER_ERROR;
    }

    /**
     * get signature value from mf file and verify it.
     *
     * @param zipFile zipFile
     * @param entry entry
     * @return if verify success
     * @throws CMSException CMSException
     */
    private static Boolean validateSignature(ZipFile zipFile, ZipEntry entry) throws CMSException {
        StringBuffer signData = new StringBuffer();
        try (BufferedReader br = new BufferedReader(
            new InputStreamReader(zipFile.getInputStream(entry), StandardCharsets.UTF_8))) {
            String line = "";
            boolean flag = false;
            while ((line = br.readLine()) != null) {
                if (flag && !line.startsWith("---")) {
                    signData.append(line.trim());
                    continue;
                }
                if (line.startsWith("---")) {
                    flag = true;
                }
            }
        } catch (IOException e) {
            LOGGER.error("io exception, {}", e);
        }

        String signStr = new String(signData).trim();
        if (null == signStr || "" == signStr) {
            LOGGER.error("signature value is null.");
            return false;
        }
        return signedDataVerify(signStr.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * verify signature value.
     *
     * @param signedData signature value
     * @return if verify sucess
     * @throws CMSException CMSException
     */
    public static boolean signedDataVerify(byte[] signedData) throws CMSException {
        CMSSignedData cmsData = new CMSSignedData(Base64.decode(signedData));
        Store store = cmsData.getCertificates();
        SignerInformationStore signerInfo = cmsData.getSignerInfos();
        Collection signers = signerInfo.getSigners();
        Iterator iterator = signers.iterator();
        while (iterator.hasNext()) {
            SignerInformation signer = (SignerInformation) iterator.next();
            Collection certs = store.getMatches(signer.getSID());
            Iterator certIterator = certs.iterator();
            X509CertificateHolder certHolder = (X509CertificateHolder) certIterator.next();
            X509Certificate cert = null;
            try {
                cert = new JcaX509CertificateConverter().setProvider("BC").getCertificate(certHolder);
                if (signer.verify(new JcaSimpleSignerInfoVerifierBuilder().setProvider("BC").build(cert))) {
                    return true;
                }
            } catch (CertificateException e) {
                LOGGER.error("certificate exception, {}", e);
            } catch (OperatorCreationException e) {
                LOGGER.error("operator create exception, {}", e);
            }

        }
        return false;
    }

}
