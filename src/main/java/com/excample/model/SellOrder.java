package com.excample.model;

import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.jcajce.provider.digest.SHA256;
import org.web3j.crypto.Hash;

import net.arnx.jsonic.JSON;

public class SellOrder {
	public String Price;
	public String Type;
	public Integer Nonce;
	public Integer TokenId;

	public static String ToHexString(byte[] input, int offset, int length, boolean withPrefix) {
		StringBuilder stringBuilder = new StringBuilder();
		if (withPrefix) {
			stringBuilder.append("0x");
		}
		for (int i = offset; i < offset + length; i++) {
			stringBuilder.append(String.format("%02x", input[i] & 0xFF));
		}

		return stringBuilder.toString();
	}

	public static String ToHexString(byte[] input) {
		return ToHexString(input, 0, input.length, false);
	}

	public byte[] hash() {
		String text = "{\"Price\":\""+Price+"\",\"Type\":\""+Type+"\",\"Nonce\":"+Nonce+",\"TokenId\":"+TokenId+"}";
		byte[] hash = Hash.sha256(text.getBytes());
		System.out.println("hash \t "+ToHexString(hash));
		return Hash.sha256(text.getBytes());
	}
}
