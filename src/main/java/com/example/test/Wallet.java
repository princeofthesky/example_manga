package com.example.test;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Arrays;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
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

	public static String privateKey = "034190447fecfe7c9a4cc04e865766b13eac198eec296688df9d3ead6394eb5f";
	public static String pubkey=Keys.getAddress(Credentials.create(privateKey).getEcKeyPair().getPublicKey());  //0x399ebc77E17D99Bc3f7345718e080ffF380c391A
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
	
	
	public static int getNonce() throws Exception {
		DefaultHttpClient client = new DefaultHttpClient();// Open a client HTTP request
		HttpGet httpGet = new HttpGet("http://nft.skymeta.pro/users/"+pubkey	+ "/nonce");
		CloseableHttpResponse response = client.execute(httpGet);
		Map<String,Object> map =JSON.decode(response.getEntity().getContent());
		int nonce = ((BigDecimal) map.get("data")).intValue();
		return nonce;
	}

	public static void sellNFT(Integer tokenId) throws Exception {
		ECKeyPair keyPaire = Credentials.create(privateKey)
				.getEcKeyPair();
		System.out.println(Keys.getAddress(keyPaire.getPublicKey()));

		SellOrder nft = new SellOrder();
		nft.TokenId = tokenId;
		nft.Nonce = getNonce();
		nft.Price = "100000000";
		nft.Type = "BUSD";

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

		System.out.println("body \t"+body);
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost("http://nft.skymeta.pro/users/sell_nft");
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
		builder.addTextBody("address", pubkey);// Setting Request Parameters
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

		
		sellNFT(9);
		
//		uploadNFT("/home/tamnb/Pictures/victor-duenas-teixeira-lbEUk6kecE8-unsplash.jpg");

	}

}
