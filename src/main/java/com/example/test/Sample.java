package com.example.test;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;
import org.web3j.crypto.Sign.SignatureData;

public class Sample {
	

	public static void NewWallet() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
		ECKeyPair keyPaire = Keys.createEcKeyPair();
				
		
		System.out.println("private \t"+keyPaire.getPrivateKey().toString(16));
		System.out.println("publix aaaaaaaaaaa\t"+Keys.getAddress(keyPaire.getPublicKey()));
				
	}
	
	public static void signMessage() throws Exception{
		System.out.println("1. Hello function " + System.currentTimeMillis());
		ECKeyPair keyPaire = Keys.createEcKeyPair();
		byte[] message = "aldsdkjaskdjklsad".getBytes() ;
		
		SignatureData signData= Sign.signMessage(message, keyPaire);
		BigInteger publicKeydatta = Sign.signedMessageToKey(message, signData);
		

		System.out.println("publix aaaaaaaaaaa\t"+Keys.getAddress(keyPaire.getPublicKey()));
		System.out.println("public key \t"+Keys.getAddress(publicKeydatta));
		
	}
	
	
	public static void main(String[] args) throws Exception {
		System.out.println("Hello world");
		
		signMessage();
		 

	}
	

}
