package com.example.test;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Arrays;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.bouncycastle.jce.interfaces.ECKey;
import org.bouncycastle.math.ec.custom.sec.SecP256K1Point;
import org.bouncycastle.util.encoders.HexEncoder;
import org.web3j.abi.datatypes.Address;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;
import org.web3j.crypto.Sign.SignatureData;
import org.web3j.crypto.WalletFile.Crypto;
import org.web3j.rlp.RlpString;

import com.example.model.NFTOrder;
import com.example.model.SellOrder;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;

import net.arnx.jsonic.JSON;

public class Wallet {

	public static void NewWallet()
			throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
		ECKeyPair keyPaire = Keys.createEcKeyPair();

		System.out.println("private \t" + keyPaire.getPrivateKey().toString(16));
		System.out.println("publix aaaaaaaaaaa\t" + Keys.getAddress(keyPaire.getPublicKey()));

	}

	public static byte[] trimLeadingBytes(byte[] bytes, byte b) {
		int offset = 0;
		for (; offset < bytes.length - 1; offset++) {
			if (bytes[offset] != b) {
				break;
			}
		}
		return Arrays.copyOfRange(bytes, offset, bytes.length);
	}

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

	public static byte[] trimLeadingZeroes(byte[] bytes) {
		return trimLeadingBytes(bytes, (byte) 0);
	}

	public static void signMessage() throws Exception {
		ECKeyPair keyPaire = Credentials.create("6ebeb334ed66b6fc281ec4056d1291dfeb95ff2f097a7aafa4ed322a43fb973c")
				.getEcKeyPair();
		System.out.println(Keys.getAddress(keyPaire.getPublicKey()));

		SellOrder nft = new SellOrder();
		nft.TokenId = 1;
		nft.Nonce = 1;
		nft.Price = "100000000";
		nft.Type = "USDT";

		SignatureData signData = Sign.signMessage(nft.hash(), keyPaire, false);

		NFTOrder order = new NFTOrder();
		order.NFT = nft;
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		outputStream.write(signData.getR());
		outputStream.write(signData.getS());
		outputStream.write(new byte[] { (byte) (signData.getV()[0] - 27) });

		byte[] sign = outputStream.toByteArray();

		order.Hash = ToHexString(sign);
		String body = JSON.encode(order);

		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost("http://127.0.0.1:9090/manga/sell");
		StringEntity entity = new StringEntity(body);
		httpPost.setEntity(entity);
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Content-type", "application/json");
		CloseableHttpResponse response = client.execute(httpPost);
		byte[] res = response.getEntity().getContent().readAllBytes();
		System.out.println(response.getEntity().getContentEncoding());
		System.out.println(new String(res));
		client.close();

	}

	public static void uploadNFT(String fileName) throws Exception {
		DefaultHttpClient client = new DefaultHttpClient();// Open a client HTTP request
		HttpPost post = new HttpPost("http://nft.skymeta.pro/manga/upload");// Create HTTP POST requests
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.setCharset(Charset.forName("utf-8"));// Set the encoding format of the request
		builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);// Setting Browser Compatibility Mode
		int count = 0;
		File file = new File(fileName);
		builder.addBinaryBody("file", file);
		builder.addTextBody("title", "My Document");// Setting Request Parameters
		builder.addTextBody("author", "Matt Aimonetti");// Setting Request Parameters
		builder.addTextBody("description", "A document with all the Go programming language secrets");// Setting Request
																										// Parameters
		builder.addTextBody("address", "0xe7FBbf888fAe89390E57d476904Af9587e3dB1B1");// Setting Request Parameters
		HttpEntity entity = builder.build();// Generating HTTP POST Entities
		post.setEntity(entity);// Setting Request Parameters
		CloseableHttpResponse response = client.execute(post);// Initiate the request and return the response
		// to the request
		if (response.getStatusLine().getStatusCode() == 200) {
			InputStreamReader isReader = new InputStreamReader(response.getEntity().getContent());
			// Creating a BufferedReader object
			BufferedReader reader = new BufferedReader(isReader);
			StringBuffer sb = new StringBuffer();
			String str;
			while ((str = reader.readLine()) != null) {
				sb.append(str);
			}
			System.out.println(sb.toString());
		}
	}

	public static void main(String[] args) throws Exception {
		System.out.println("Hello world");

		uploadNFT("/home/tamnb/Pictures/test1.png");

	}

}
