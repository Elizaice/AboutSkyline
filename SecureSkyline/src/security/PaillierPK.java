package security;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigInteger;

public class PaillierPK implements Serializable
{
	// k1 is the security parameter. It is the number of bits in n.
	public int k1 = 1024;
	
	public PaillierPK (int n)
	{
		k1 = n;
	}

	// n = pq is a product of two large primes (such N is known as RSA modulous.
    public BigInteger n;
    public BigInteger modulus;
    
    private static final long serialVersionUID = -4979802656002515205L;

    private void readObject(ObjectInputStream aInputStream) throws ClassNotFoundException,
            IOException
    {
        // always perform the default de-serialization first
        aInputStream.defaultReadObject();
    }

    private void writeObject(ObjectOutputStream aOutputStream) throws IOException
    {
        // perform the default serialization for all non-transient, non-static
        // fields
        aOutputStream.defaultWriteObject();
    }

    public String toString()
    {
        return "k1 = " + k1 + ", n = " + n + ", modulus = " + modulus;
    }
}