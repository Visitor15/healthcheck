package mobile.forged.com.health.utilities;

import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

/**
 * Utility class supporting data encryption and decryption.
 */
public final class EncryptionHelper
{
    // [region] constants

	private static final String ALGORITHM = "PBEWithMD5AndDES";
	private static final String ENCODING = "utf-8";

	// [endregion]


	// [region] instance variables

	private final SecretKey _cipherKey;
	private final AlgorithmParameterSpec _cipherSpec;

	// [endregion]


	// [region] constructors

	/**
	 * Constructs a new instance of the encryption helper.
	 * 
	 * @param context
	 *            context providing access to system resources.
	 * @param password
	 *            the encryption secret key used for all generated ciphers.
	 */
	public EncryptionHelper(String salt, String password)
	{
		// initialize instance variables
		try
		{
			SecretKeyFactory keyFactory =
				SecretKeyFactory.getInstance(ALGORITHM);
			_cipherKey =
				keyFactory
					.generateSecret(new PBEKeySpec(password.toCharArray()));
			final byte[] saltBytes = new byte[8];
			final byte[] saltSource = salt.getBytes(ENCODING);
			for (int i = 0; i < 8; ++i)
			{
				saltBytes[i] = saltSource[i];
			}
			_cipherSpec = new PBEParameterSpec(saltBytes, 20);
		}
		catch (RuntimeException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	// [endregion]


	// [region] public methods

	/**
	 * Constructs a new encryption or decryption cipher.
	 * 
	 * @param cipherMode
	 *            either {@link Cipher#ENCRYPT_MODE} or
	 *            {@link Cipher#DECRYPT_MODE}.
	 */
	public Cipher createCipher(int cipherMode)
	{
		try
		{
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(cipherMode, _cipherKey, _cipherSpec);
			return cipher;
		}
		catch (RuntimeException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 * Encrypts the specified string.
	 * 
	 * @param value
	 *            decrypted string to be encrypted.
	 */
	public String encrypt(String value)
	{
		try
		{
			// create cipher
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, _cipherKey, _cipherSpec);

			// encrypt string
			final byte[] bytes =
				value == null ? new byte[0] : value.getBytes(ENCODING);
			return android.util.Base64.encodeToString(cipher.doFinal(bytes), android.util.Base64.DEFAULT);
		}
		catch (RuntimeException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 * Decrypts the specified string.
	 * 
	 * @param value
	 *            encrypted string to be decrypted.
	 */
	public String decrypt(String value)
	{
		try
		{
			// create cipher
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, _cipherKey, _cipherSpec);

			// decrypt string
			final byte[] bytes =
				value == null ? null : android.util.Base64.decode(value, android.util.Base64.DEFAULT);
			return new String(cipher.doFinal(bytes), ENCODING);
		}
		catch (RuntimeException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	// [endregion]

} // class EncryptionHelper