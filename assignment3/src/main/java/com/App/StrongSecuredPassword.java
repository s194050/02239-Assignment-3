package com.App;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class StrongSecuredPassword {
    public static boolean validatePassword(String originalPassword, String storedPassword) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String[] parts = storedPassword.split(":");
        int iterations = Integer.parseInt(parts[0]);
        byte[] salt = fromHex(parts[1]);
        byte[] hash = fromHex(parts[2]);

        // Compute the hash of the provided password, using the same salt
        PBEKeySpec spec = new PBEKeySpec(originalPassword.toCharArray(), salt, iterations, hash.length * 8);

        // Generate the hash
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

        // Compute the hash of the provided password, using the same salt,
        byte[] testHash = skf.generateSecret(spec).getEncoded();

        // compare lengths of the hash and the test hash
        int diff = hash.length ^ testHash.length;

        // XOR the two hashes to see if they are the same
        for (int i = 0; i < hash.length && i < testHash.length; i++) {
            diff |= hash[i] ^ testHash[i];
        }

        // if the diff is 0, then the password is correct
        return diff == 0;
    }

    public static String generateStorngPasswordHash(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // set iterations to set the difficulty of the hash
        int iterations = 1000;
        char[] chars = password.toCharArray();
        // generate a random salt
        byte[] salt = getSalt().getBytes();

        // PBKDF2WithHmacSHA1 is the algorithm used to generate the hash
        PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);
        // generate the hash
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        // get the hash
        byte[] hash = skf.generateSecret(spec).getEncoded();
        // return the hash
        return iterations + ":" + toHex(salt) + ":" + toHex(hash);

    }

    private static String getSalt() throws NoSuchAlgorithmException {
        // generate a random salt
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt.toString();
    }

    private static String toHex(byte[] array) throws NoSuchAlgorithmException {
        // convert the byte array to a hex string
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);
        int paddingLength = (array.length * 2) - hex.length();
        if (paddingLength > 0) {
            return String.format("%0" + paddingLength + "d", 0) + hex;
        } else {
            return hex;
        }
    }

    private static byte[] fromHex(String hex) throws NoSuchAlgorithmException {
        // convert the hex string to a byte array
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2),
                    16);
        }
        return bytes;
    }
}

// thanks to: https://github.com/lokeshgupta1981/Core-Java/blob/master/src/main/java/com/howtodoinjava/hashing/password/demo/advanced/ReallyStrongSecuredPassword.java