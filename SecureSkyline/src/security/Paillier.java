package security;

import java.math.BigInteger;
import java.util.Random;

public class Paillier
{
	// k2 controls the error probability of the primality testing algorithm
	// (specifically, with probability at most 2^(-k2) a NON prime is chosen).
	private static int k2 = 40;
	private static Random rnd = new Random();

	public static void keyGen(PaillierSK sk, PaillierPK pk)
	{
		// bit_length is set as half of k1 so that when pq is computed, the
		// result has k1 bits

		// Passing in PrivateKey(1,024)
		int bit_length = sk.k1/2;
		System.out.println("Bit length: " + bit_length);
		System.out.println("Random: " + rnd);
		System.out.println("End of Random");

		// Chooses a random prime of length k2. The probability that
		// p is not prime is at most 2^(-k2)
		BigInteger p = new BigInteger(bit_length, k2, rnd);//(512,40,random)
		BigInteger q = new BigInteger(bit_length, k2, rnd);//(512,40,random)
		
		// System.out.println("Key P: " + p);
		// System.out.println("Key Q: " + q);

		// Modifications to the public key
		pk.k1 = sk.k1;
		pk.n = p.multiply(q); // n = pq
		pk.modulus = pk.n.multiply(pk.n); // modulous = n^2

		// Modifications to the Private key
		sk.lambda = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
		sk.mu = sk.lambda.modInverse(pk.n);
		sk.n = pk.n;
		sk.modulus = pk.modulus;
	}

	// Compute ciphertext = (mn+1)r^n (mod n^2) in two stages: (mn+1) and (r^n).
	public static BigInteger encrypt(BigInteger plaintext, PaillierPK pk)
	{
		BigInteger randomness = new BigInteger(pk.k1, rnd);
		BigInteger tmp1 = plaintext.multiply(pk.n).add(BigInteger.ONE).mod(pk.modulus);
		BigInteger tmp2 = randomness.modPow(pk.n, pk.modulus);
		BigInteger ciphertext = tmp1.multiply(tmp2).mod(pk.modulus);
		return ciphertext;
	}
	
	public static BigInteger encrypt(long plaintext, PaillierPK pk)
	{
		return encrypt(BigInteger.valueOf(plaintext), pk);
	}

	// Compute plaintext = L(cipherText^(lambda) mod n^2) * mu mod n
	public static BigInteger decrypt(BigInteger ciphertext, PaillierSK sk)
	{
		// L(u) = (u-1)/n
		return L(ciphertext.modPow(sk.lambda, sk.modulus), sk.n).multiply(sk.mu).mod(sk.n);
	}

	// On input two encrypted values, returns an encryption of the sum of the values
	public static BigInteger add(BigInteger ciphertext1, BigInteger ciphertext2, PaillierPK pk)
	{
		//(Cipher1 * Cipher 2 (mod N)
		return ciphertext1.multiply(ciphertext2).mod(pk.modulus);
	}
	
	public static BigInteger summation(BigInteger [] values, PaillierPK pk)
	{
		BigInteger ciphertext = values[0];
		for (int i = 1; i < values.length; i++)
		{
			ciphertext = ciphertext.multiply(values[i]).mod(pk.modulus);
		}
		return ciphertext;
	}
	
	public static BigInteger summation(BigInteger [] values, PaillierPK pk, int limit)
	{
		if (limit > values.length)
		{
			return summation(values, pk);
		}
		BigInteger ciphertext = values[0];
		for (int i = 1; i < values.length; i++)
		{
			ciphertext = ciphertext.multiply(values[i]).mod(pk.modulus);
		}
		return ciphertext;
	}

	public static BigInteger subtract(BigInteger ciphertext1, BigInteger ciphertext2, PaillierPK pk)
	{
		ciphertext2 = Paillier.multiply(ciphertext2, -1, pk);
		BigInteger ciphertext = ciphertext1.multiply(ciphertext2).mod(pk.modulus);
		return ciphertext;
	}

	// On input an encrypted value x and a scalar c, returns an encryption of cx.
	public static BigInteger multiply(BigInteger ciphertext1, BigInteger scalar, PaillierPK pk)
	{
		BigInteger ciphertext = ciphertext1.modPow(scalar, pk.modulus);
		return ciphertext;
	}

	public static BigInteger multiply(BigInteger ciphertext1, long scalar, PaillierPK pk)
	{
		return multiply(ciphertext1, BigInteger.valueOf(scalar), pk);
	}

	/*
	 * Please note: Divide will only work correctly on perfect divisor
	 * 2|20, it will work.
	 * if you try 3|20, it will NOT work and you will get a wrong answer!
	 * 
	 * If you want to do 3|20, you MUST use a division protocol from Veugen paper
	 */
	public static BigInteger divide(BigInteger ciphertext, long divisor, PaillierPK pk)
	{
		return divide(ciphertext, BigInteger.valueOf(divisor), pk);
	}
	
	public static BigInteger divide(BigInteger ciphertext, BigInteger divisor, PaillierPK pk)
	{
		divisor = divisor.modInverse(pk.modulus);
		return multiply(ciphertext, divisor, pk);
	}

	// L(u)=(u-1)/n
	private static BigInteger L(BigInteger u, BigInteger n)
	{
		return u.subtract(BigInteger.ONE).divide(n);
	}

	public static BigInteger reRandomize(BigInteger ciphertext, PaillierPK pk)
	{
		return Paillier.add(ciphertext, Paillier.encrypt(BigInteger.ZERO, pk), pk);
	}
}